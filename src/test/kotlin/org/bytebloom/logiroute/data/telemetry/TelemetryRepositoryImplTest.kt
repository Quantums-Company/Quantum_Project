package org.bytebloom.logiroute.data.telemetry

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.junit.jupiter.api.Test

class TelemetryRepositoryImplTest {

    private class FakeTelemetryDataSource(
        private val updates: List<VehicleTelemetry>
    ) : TelemetryDataSource {

        var requestedVehicleIds: List<String> = emptyList()
            private set

        override fun streamTelemetry(vehicleIds: List<String>): Flow<VehicleTelemetry> {
            requestedVehicleIds = vehicleIds
            return flowOf(*updates.toTypedArray())
        }
    }

    @Test
    fun `should stream telemetry updates from data source`() = runTest {
        // Given
        val update = VehicleTelemetry("TRK-001", 31.5, 35.0, 40.0, 80.0)
        val repository = TelemetryRepositoryImpl(FakeTelemetryDataSource(listOf(update)))

        // When
        val updates = repository.streamTelemetry(listOf("TRK-001")).toList()

        // Then
        assertThat(updates).containsExactly(update)
    }

    @Test
    fun `should pass requested vehicle ids to data source`() = runTest {
        // Given
        val dataSource = FakeTelemetryDataSource(emptyList())
        val repository = TelemetryRepositoryImpl(dataSource)

        // When
        repository.streamTelemetry(listOf("TRK-001", "TRK-002")).toList()

        // Then
        assertThat(dataSource.requestedVehicleIds).containsExactly("TRK-001", "TRK-002").inOrder()
    }
}