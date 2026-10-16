package data.repository.telemetry

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bytebloom.data.repository.telemetry.TelemetryRepositoryImpl
import org.bytebloom.data.source.telemetry.TelemetryDataSource
import org.bytebloom.domain.model.VehicleTelemetry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
    fun `streams telemetry updates from the data source`() = runTest {
        // Given
        val update = VehicleTelemetry("TRK-001", 31.5, 35.0, 40.0, 80.0)
        val repository = TelemetryRepositoryImpl(FakeTelemetryDataSource(listOf(update)))

        // When
        val updates = repository.streamTelemetry(listOf("TRK-001")).toList()

        // Then
        assertEquals(listOf(update), updates)
    }

    @Test
    fun `passes the requested vehicle ids to the data source`() = runTest {
        // Given
        val dataSource = FakeTelemetryDataSource(emptyList())
        val repository = TelemetryRepositoryImpl(dataSource)

        // When
        repository.streamTelemetry(listOf("TRK-001", "TRK-002")).toList()

        // Then
        assertEquals(listOf("TRK-001", "TRK-002"), dataSource.requestedVehicleIds)
    }
}