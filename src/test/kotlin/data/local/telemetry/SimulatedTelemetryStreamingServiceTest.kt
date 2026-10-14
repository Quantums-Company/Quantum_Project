package data.local.telemetry

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bytebloom.data.local.telemetry.SimulatedTelemetryStreamingService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class SimulatedTelemetryStreamingServiceTest {

    @Test
    fun `emits requested number of telemetry updates`() = runTest {
        // Given
        val service = SimulatedTelemetryStreamingService(
            interval = 1.seconds
        )

        // When
        val updates = service
            .streamTelemetry(listOf("TRK-001"))
            .take(2)
            .toList()

        // Then
        assertEquals(2, updates.size)
    }

    @Test
    fun `emits telemetry for each requested vehicle`() = runTest {
        // Given
        val service = SimulatedTelemetryStreamingService()

        // When
        val updates = service
            .streamTelemetry(listOf("TRK-001", "TRK-002"))
            .take(2)
            .toList()

        // Then
        assertEquals(
            setOf("TRK-001", "TRK-002"),
            updates.map { it.vehicleId }.toSet()
        )
    }

    @Test
    fun `emits valid GPS coordinates and fuel percentage`() = runTest {
        // Given
        val service = SimulatedTelemetryStreamingService()

        // When
        val updates = service
            .streamTelemetry(listOf("TRK-001"))
            .take(2)
            .toList()

        // Then
        assertTrue(
            updates.all {
                it.latitude in -90.0..90.0 &&
                        it.longitude in -180.0..180.0 &&
                        it.fuelPercent in 0.0..100.0
            }
        )
    }

    @Test
    fun `emits no updates for an empty fleet`() = runTest {
        // Given
        val service = SimulatedTelemetryStreamingService()

        // When
        val updates = service
            .streamTelemetry(emptyList())
            .toList()

        // Then
        assertTrue(updates.isEmpty())
    }
}
