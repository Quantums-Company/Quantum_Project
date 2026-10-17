package org.bytebloom.logiroute.data.telemetry

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SimulatedTelemetryDataSourceTest {

    @Test
    fun `should emit requested number of telemetry updates`() = runTest {
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
        assertThat(updates).hasSize(2)
    }

    @Test
    fun `should emit telemetry for each requested vehicle`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource()

        // When
        val updates = service
            .streamTelemetry(listOf("TRK-001", "TRK-002"))
            .take(2)
            .toList()

        // Then
        assertThat(updates.map { it.vehicleId }).containsExactly("TRK-001", "TRK-002")
    }

    @Test
    fun `should emit valid gps coordinates and fuel percentage`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource()

        // When
        val updates = service
            .streamTelemetry(listOf("TRK-001"))
            .take(2)
            .toList()

        // Then
        assertThat(
            updates.all {
                it.latitude in -90.0..90.0 &&
                        it.longitude in -180.0..180.0 &&
                        it.fuelPercent in 0.0..100.0
            }
        ).isTrue()
    }

    @Test
    fun `should emit no updates when fleet is empty`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource()

        // When
        val updates = service
            .streamTelemetry(emptyList())
            .toList()

        // Then
        assertThat(updates).isEmpty()
    }

    @Test
    fun `should emit first update immediately without waiting`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.seconds)

        // When
        service.streamTelemetry(listOf("TRK-001")).first()

        // Then
        assertThat(currentTime).isEqualTo(0L)
    }

    @Test
    fun `should wait one interval between consecutive updates`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.seconds)

        // When
        service.streamTelemetry(listOf("TRK-001")).take(3).toList()

        // Then
        assertThat(currentTime).isEqualTo(2_000L)
    }

    @Test
    fun `should burn one percent of fuel per update`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.milliseconds)

        // When
        val updates = service.streamTelemetry(listOf("TRK-001")).take(3).toList()

        // Then
        assertThat(updates.map { it.fuelPercent }).containsExactly(100.0, 99.0, 98.0).inOrder()
    }

    @Test
    fun `should keep fuel at zero when tank is empty`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.milliseconds)

        // When
        val updates = service.streamTelemetry(listOf("TRK-001")).take(150).toList()

        // Then
        assertThat(updates.takeLast(50).map { it.fuelPercent }).containsExactlyElementsIn(List(50) { 0.0 })
    }

    @Test
    fun `should keep vehicle stopped for several consecutive updates`() = runTest {
        // Given
        val service = SimulatedTelemetryDataSource(interval = 1.milliseconds)

        // When
        val updates = service.streamTelemetry(listOf("TRK-001")).take(5).toList()

        // Then
        assertThat(updates.map { it.speedKph == 0.0 }).containsExactly(true, true, true, false, false).inOrder()
    }
}