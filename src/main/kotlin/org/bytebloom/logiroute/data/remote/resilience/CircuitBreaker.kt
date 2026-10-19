package org.bytebloom.logiroute.data.remote.resilience

import java.time.Duration
import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.data.remote.exception.CallNotPermittedException
import java.io.IOException

class CircuitBreaker(
    private val failureThreshold: Int = DEFAULT_FAILURE_THRESHOLD,
    private val openDuration: Duration = DEFAULT_OPEN_DURATION,
    private val isNetworkFailure: (Throwable) -> Boolean = { error ->
        generateSequence(error) { it.cause }.any { it is IOException }
    },
    private val clockNanos: () -> Long = System::nanoTime
) {
    companion object {
        private const val DEFAULT_FAILURE_THRESHOLD = 5
        private val DEFAULT_OPEN_DURATION: Duration = Duration.ofSeconds(5)
    }

    private val lock = Any()

    private var state = CircuitState.CLOSED
    private var consecutiveFailures = 0
    private var openedAtNanos = 0L
    private var probeInFlight = false
    private var generation = 0L

    val currentState: CircuitState
        get() = synchronized(lock) { state }

    init {
        require(failureThreshold > 0) {
            "Failure threshold must be greater than zero."
        }
        require(!openDuration.isZero && !openDuration.isNegative) {
            "Open duration must be positive."
        }
    }

    private data class Permit(
        val generation: Long,
        val isProbe: Boolean
    )

    suspend fun <T> execute(
        beforeCall: suspend () -> Unit = {},
        block: suspend () -> T
    ): T {
        ensureCallAllowed()
        beforeCall()
        val permit = acquirePermit()

        try {
            val result = block()
            onSuccess(permit)
            return result
        } catch (exception: CancellationException) {
            releaseProbe(permit)
            throw exception
        } catch (exception: Exception) {
            if (isNetworkFailure(exception)) {
                onNetworkFailure(permit)
            } else {
                onNonNetworkFailure(permit)
            }
            throw exception
        } catch (error: Throwable) {
            releaseProbe(permit)
            throw error
        }
    }

    private fun ensureCallAllowed() = synchronized(lock) {
        when (state) {
            CircuitState.CLOSED -> Unit
            CircuitState.OPEN -> {
                val elapsed = clockNanos() - openedAtNanos
                if (elapsed < openDuration.toNanos()) {
                    throw CallNotPermittedException()
                }
            }
            CircuitState.HALF_OPEN -> {
                if (probeInFlight) {
                    throw CallNotPermittedException(
                        "Circuit breaker is HALF_OPEN. A probe is already running."
                    )
                }
            }
        }
    }

    private fun acquirePermit(): Permit = synchronized(lock) {
        when (state) {
            CircuitState.CLOSED -> Permit(
                generation = generation,
                isProbe = false
            )

            CircuitState.OPEN -> {
                val elapsed = clockNanos() - openedAtNanos

                if (elapsed < openDuration.toNanos()) {
                    throw CallNotPermittedException()
                }

                state = CircuitState.HALF_OPEN
                generation++
                probeInFlight = true

                Permit(
                    generation = generation,
                    isProbe = true
                )
            }

            CircuitState.HALF_OPEN -> {
                if (probeInFlight) {
                    throw CallNotPermittedException(
                        "Circuit breaker is HALF_OPEN. A probe is already running."
                    )
                }

                probeInFlight = true

                Permit(
                    generation = generation,
                    isProbe = true
                )
            }
        }
    }

    private fun onSuccess(permit: Permit) {
        synchronized(lock) {
            if (permit.generation != generation) return

            when (state) {
                CircuitState.CLOSED -> {
                    consecutiveFailures = 0
                }

                CircuitState.HALF_OPEN -> {
                    if (permit.isProbe) {
                        state = CircuitState.CLOSED
                        consecutiveFailures = 0
                        probeInFlight = false
                        generation++
                    }
                }

                CircuitState.OPEN -> Unit
            }
        }
    }

    private fun onNetworkFailure(permit: Permit) {
        synchronized(lock) {
            if (permit.generation != generation) return

            when (state) {
                CircuitState.CLOSED -> {
                    consecutiveFailures++

                    if (consecutiveFailures >= failureThreshold) {
                        openCircuit()
                    }
                }

                CircuitState.HALF_OPEN -> {
                    if (permit.isProbe) {
                        openCircuit()
                    }
                }

                CircuitState.OPEN -> Unit
            }
        }
    }

    private fun onNonNetworkFailure(permit: Permit) {
        synchronized(lock) {
            if (permit.generation != generation) return

            when (state) {
                CircuitState.CLOSED -> {
                    consecutiveFailures = 0
                }

                CircuitState.HALF_OPEN -> {
                    if (permit.isProbe) {
                        openCircuit()
                    }
                }

                CircuitState.OPEN -> Unit
            }
        }
    }

    private fun releaseProbe(permit: Permit) {
        synchronized(lock) {
            if (
                permit.generation == generation &&
                state == CircuitState.HALF_OPEN &&
                permit.isProbe
            ) {
                probeInFlight = false
            }
        }
    }

    private fun openCircuit() {
        state = CircuitState.OPEN
        consecutiveFailures = 0
        openedAtNanos = clockNanos()
        probeInFlight = false
        generation++
    }
}