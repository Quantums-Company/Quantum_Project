package org.bytebloom.logiroute.data.telemetry

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SimulatedTelemetryDataSource(
    private val interval: Duration = DEFAULT_INTERVAL
) : TelemetryDataSource {
    companion object {
        private val DEFAULT_INTERVAL = 1.seconds
        private const val BASE_SPEED_KPH = 30.0
        private const val MAX_FUEL_PERCENT = 100.0
        private const val MIN_FUEL_PERCENT = 0.0
        private const val FUEL_BURN_PERCENT_PER_TICK = 1.0
        private const val LATITUDE_BASE = 31.5
        private const val LONGITUDE_BASE = 35.0
        private const val OFFSET_STEP = 0.01
        private const val TICK_STEP = 0.0001
        private const val STOP_CYCLE_TICKS = 20
        private const val STOP_DURATION_TICKS = 3
        private const val SPEED_VARIATION_RANGE = 20
    }

    init {
        require(interval.isPositive()) {
            "Telemetry interval must be positive."
        }
    }

    override fun streamTelemetry(
        vehicleIds: List<String>
    ): Flow<VehicleTelemetry> = flow {
        if (vehicleIds.isEmpty()) return@flow

        var tick = 0

        while (true) {
            vehicleIds.forEachIndexed { index, vehicleId ->
                val speed = calculateSimulatedSpeed(tick, index)

                emit(
                    VehicleTelemetry(
                        vehicleId = vehicleId,
                        latitude = LATITUDE_BASE + index * OFFSET_STEP + tick * TICK_STEP,
                        longitude = LONGITUDE_BASE + index * OFFSET_STEP + tick * TICK_STEP,
                        speedKph = speed,
                        fuelPercent = calculateRemainingFuel(tick)
                    )
                )
            }

            if (calculateRemainingFuel(tick) <= MIN_FUEL_PERCENT) return@flow  // tank is empty: the stream ends

            if (calculateRemainingFuel(tick) <= MIN_FUEL_PERCENT) return@flow  // tank is empty: the stream ends

            tick++

            delay(interval)
        }
    }

    private fun calculateSimulatedSpeed(tick: Int, index: Int): Double {
        val cycle = tick + index
        return if (cycle % STOP_CYCLE_TICKS < STOP_DURATION_TICKS) {
            0.0
        } else {
            BASE_SPEED_KPH + (cycle % SPEED_VARIATION_RANGE)
        }
    }

    private fun calculateRemainingFuel(tick: Int): Double =
        (MAX_FUEL_PERCENT - tick * FUEL_BURN_PERCENT_PER_TICK).coerceAtLeast(MIN_FUEL_PERCENT)
}