package data.local.telemetry

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.telemetry.SimulatedTelemetryDataSource
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SimulatedTelemetryDataSourceTest {

    @Test
    fun `emits requested number of telemetry updates`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(
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
        val service = SimulatedTelemetryDataSource()

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
        val service = SimulatedTelemetryDataSource()

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
        val service = SimulatedTelemetryDataSource()

        // When
        val updates = service
            .streamTelemetry(emptyList())
            .toList()

        // Then
        assertTrue(updates.isEmpty())
    }

    @Test
    fun `emits the first update immediately without waiting`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.seconds)

        // When
        service.streamTelemetry(listOf("TRK-001")).first()

        // Then
        assertEquals(0L, currentTime)
    }

    @Test
    fun `waits one interval between consecutive updates`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.seconds)

        // When
        service.streamTelemetry(listOf("TRK-001")).take(3).toList()

        // Then
        assertEquals(2_000L, currentTime)
    }

    @Test
    fun `burns one percent of fuel per update`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.milliseconds)

        // When
        val updates = service.streamTelemetry(listOf("TRK-001")).take(3).toList()

        // Then
        assertEquals(listOf(100.0, 99.0, 98.0), updates.map { it.fuelPercent })
    }

    @Test
    fun `fuel stays at zero instead of refilling`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.milliseconds)

        // When
        val updates = service.streamTelemetry(listOf("TRK-001")).take(150).toList()

        // Then
        assertEquals(List(50) { 0.0 }, updates.takeLast(50).map { it.fuelPercent })
    }

    @Test
    fun `vehicle stays stopped for several consecutive updates`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.milliseconds)

        // When
        val updates = service.streamTelemetry(listOf("TRK-001")).take(5).toList()

        // Then
        assertEquals(listOf(true, true, true, false, false), updates.map { it.speedKph == 0.0 })
    }
}