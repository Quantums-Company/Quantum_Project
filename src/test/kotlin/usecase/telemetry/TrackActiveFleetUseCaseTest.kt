package usecase.telemetry

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.model.result.VehicleDelayNotification
import org.bytebloom.logiroute.domain.repository.TelemetryRepository
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrackActiveFleetUseCaseTest {

    private class FakeTelemetryRepository(
        private val updates: List<VehicleTelemetry>
    ) : TelemetryRepository {

        override fun streamTelemetry(
            vehicleIds: List<String>
        ): Flow<VehicleTelemetry> = updates.asFlow()
    }

    @Test
    fun `emits notification for active vehicle moving below threshold`() = runTest {
        // Given
        val repository = FakeTelemetryRepository(
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
        val useCase = TrackActiveFleetUseCase(repository)

        // When
        val notifications = useCase(setOf("TRK-001")).toList()

        // Then
        assertEquals(1, notifications.size)
    }

    @Test
    fun `does not notify for vehicle moving above threshold`() = runTest {
        // Given
        val repository = FakeTelemetryRepository(
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
        val useCase = TrackActiveFleetUseCase(repository)

        // When
        val notifications = useCase(setOf("TRK-001")).toList()

        // Then
        assertEquals(0, notifications.size)
    }

    @Test
    fun `ignores updates from vehicles outside active fleet`() = runTest {
        // Given
        val repository = FakeTelemetryRepository(
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
        val useCase = TrackActiveFleetUseCase(repository)

        // When
        val notifications = useCase(setOf("TRK-001")).toList()

        // Then
        assertEquals(0, notifications.size)
    }

    @Test
    fun `returns no notifications when active fleet is empty`() = runTest {
        // Given
        val repository = FakeTelemetryRepository(emptyList())
        val useCase = TrackActiveFleetUseCase(repository)

        // When
        val notifications = useCase(emptySet()).toList()

        // Then
        assertEquals(emptyList<VehicleDelayNotification>(), notifications)
    }

    @Test
    fun `notifies only once while the vehicle stays delayed`() = runTest {
        // Given
        val repository = FakeTelemetryRepository(
            listOf(telemetry(speedKph = 0.0), telemetry(speedKph = 0.0), telemetry(speedKph = 3.0))
        )
        val useCase = TrackActiveFleetUseCase(repository)

        // When
        val notifications = useCase(setOf("TRK-001")).toList()

        // Then
        assertEquals(1, notifications.size)
    }

    @Test
    fun `notifies again after the vehicle recovers and gets delayed again`() = runTest {
        // Given
        val repository = FakeTelemetryRepository(
            listOf(telemetry(speedKph = 0.0), telemetry(speedKph = 40.0), telemetry(speedKph = 1.0))
        )
        val useCase = TrackActiveFleetUseCase(repository)

        // When
        val notifications = useCase(setOf("TRK-001")).toList()

        // Then
        assertEquals(2, notifications.size)
    }

    @Test
    fun `tracks each vehicle delay independently`() = runTest {
        // Given
        val repository = FakeTelemetryRepository(
            listOf(
                telemetry(vehicleId = "TRK-001", speedKph = 0.0),
                telemetry(vehicleId = "TRK-002", speedKph = 0.0),
                telemetry(vehicleId = "TRK-001", speedKph = 0.0)
            )
        )
        val useCase = TrackActiveFleetUseCase(repository)

        // When
        val notifications = useCase(setOf("TRK-001", "TRK-002")).toList()

        // Then
        assertEquals(listOf("TRK-001", "TRK-002"), notifications.map { it.vehicleId })
    }

    @Test
    fun `starts with fresh delay state for every collection`() = runTest {
        // Given
        val repository = FakeTelemetryRepository(listOf(telemetry(speedKph = 0.0)))
        val notificationsFlow = TrackActiveFleetUseCase(repository)(setOf("TRK-001"))

        // When
        val firstCollection = notificationsFlow.toList()
        val secondCollection = notificationsFlow.toList()

        // Then
        assertEquals(firstCollection, secondCollection)
    }

    private fun telemetry(vehicleId: String = "TRK-001", speedKph: Double) = VehicleTelemetry(
        vehicleId = vehicleId,
        latitude = 31.5,
        longitude = 35.0,
        speedKph = speedKph,
        fuelPercent = 80.0
    )
}