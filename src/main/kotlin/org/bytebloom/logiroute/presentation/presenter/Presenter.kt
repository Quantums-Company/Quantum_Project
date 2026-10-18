package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.presenter.mapper.toUiError

/**
 * Base class of every presenter.
 *
 * A presenter owns one immutable [state] that a UI observes, and exposes plain functions
 * ("intents") that a UI calls. It knows nothing about consoles, Compose or Android views,
 * so any UI can be plugged on top of it:
 *  - console:  collect [state], print it
 *  - Compose / Android: `presenter.state.collectAsState()`
 *
 * Every presenter runs in a child of [parentScope]; [close] cancels only this presenter's work.
 */
abstract class Presenter<S : Any>(
    initialState: S,
    parentScope: CoroutineScope
) {
    private val supervisor = SupervisorJob(parentScope.coroutineContext[Job])
    protected val scope: CoroutineScope = CoroutineScope(parentScope.coroutineContext + supervisor)

    private val mutableState = MutableStateFlow(initialState)
    val state: StateFlow<S> = mutableState.asStateFlow()

    protected fun update(transform: (S) -> S) = mutableState.update(transform)

    fun close() = supervisor.cancel()

    /**
     * Runs [block]; a failure is translated into a [UiError] and handed to [onFailure].
     * Cancellation is never swallowed, so structured concurrency keeps working.
     */
    @Suppress("TooGenericExceptionCaught")
    protected suspend fun <T> guarded(
        onFailure: (UiError) -> Unit,
        block: suspend () -> T
    ): T? =
        try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (failure: Exception) {
            onFailure(failure.toUiError())
            null
        }
}
