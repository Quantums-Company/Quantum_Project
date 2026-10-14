package usecase.telemetry

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.VehicleTelemetry
import org.bytebloom.domain.model.result.VehicleDelayNotification
import org.bytebloom.domain.service.TelemetryStreamingService
import org.bytebloom.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrackActiveFleetUseCaseTest {

    private class FakeTelemetryStreamingService(
        private val updates: List<VehicleTelemetry>
    ) : TelemetryStreamingService {

        override fun streamTelemetry(
            vehicleIds: List<String>
        ): Flow<VehicleTelemetry> = updates.asFlow()
    }

    @Test
    fun `emits notification for active vehicle moving below threshold`() = runTest {
        // Given
        val service = FakeTelemetryStreamingService(
            listOf(
                VehicleTelemetry(
                    vehicleId = "TRK-001",
                    latitude = 31.5,
                    longitude = 35.0,
                    speedKph = 0.0,
                    fuelPercent = 80.0
                )
            )
        )
        val useCase = TrackActiveFleetUseCase(service)

        // When
        val notifications = useCase(setOf("TRK-001")).toList()

        // Then
        assertEquals(1, notifications.size)
    }

    @Test
    fun `does not notify for vehicle moving above threshold`() = runTest {
        // Given
        val service = FakeTelemetryStreamingService(
            listOf(
                VehicleTelemetry(
                    vehicleId = "TRK-001",
                    latitude = 31.5,
                    longitude = 35.0,
                    speedKph = 30.0,
                    fuelPercent = 80.0
                )
            )
        )
        val useCase = TrackActiveFleetUseCase(service)

        // When
        val notifications = useCase(setOf("TRK-001")).toList()

        // Then
        assertEquals(0, notifications.size)
    }

    @Test
    fun `ignores updates from vehicles outside active fleet`() = runTest {
        // Given
        val service = FakeTelemetryStreamingService(
            listOf(
                VehicleTelemetry(
                    vehicleId = "TRK-999",
                    latitude = 31.5,
                    longitude = 35.0,
                    speedKph = 0.0,
                    fuelPercent = 80.0
                )
            )
        )
        val useCase = TrackActiveFleetUseCase(service)

        // When
        val notifications = useCase(setOf("TRK-001")).toList()

        // Then
        assertEquals(0, notifications.size)
    }

    @Test
    fun `returns no notifications when active fleet is empty`() = runTest {
        // Given
        val service = FakeTelemetryStreamingService(emptyList())
        val useCase = TrackActiveFleetUseCase(service)

        // When
        val notifications = useCase(emptySet()).toList()

        // Then
        assertEquals(emptyList<VehicleDelayNotification>(), notifications)
    }
}
