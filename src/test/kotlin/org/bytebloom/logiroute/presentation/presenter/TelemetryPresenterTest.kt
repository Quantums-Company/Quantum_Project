package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.usecase.telemetry.StreamFleetTelemetryUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.presentation.FakeTelemetryRepository
import org.bytebloom.logiroute.presentation.FakeVehicleRepository
import org.bytebloom.logiroute.presentation.endlessReadings
import org.bytebloom.logiroute.presentation.model.FleetEventUi
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.readingsEvery
import org.bytebloom.logiroute.presentation.reading
import org.bytebloom.logiroute.presentation.vehicle
import org.bytebloom.logiroute.presentation.warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TelemetryPresenterTest {

    private val hub = warehouse("WH-001")
    private val fleet = listOf(vehicle("TRK-001", hub), vehicle("TRK-002", hub))

    private fun presenter(
        scope: CoroutineScope,
        telemetry: FakeTelemetryRepository,
        vehicles: FakeVehicleRepository = FakeVehicleRepository(fleet),
        maxLogEntries: Int = 50
    ) = TelemetryPresenter(
        getAllVehicles = GetAllVehiclesUseCase(vehicles),
        streamFleetTelemetry = StreamFleetTelemetryUseCase(telemetry),
        trackActiveFleet = TrackActiveFleetUseCase(telemetry),
        parentScope = scope,
        maxLogEntries = maxLogEntries
    )

    private fun finite(vararg readings: VehicleTelemetry) =
        FakeTelemetryRepository { readingsEvery(everyMillis = 10, readings.toList()) }

    @Test
    fun `starts idle`() = runTest {
        // Given / When
        val presenter = presenter(backgroundScope, finite())

        // Then
        assertEquals(TelemetryState(), presenter.state.value)
    }

    @Test
    fun `shows starting right after start is requested`() = runTest {
        // Given
        val presenter = presenter(backgroundScope, finite(reading("TRK-001")))

        // When
        presenter.start()

        // Then
        assertEquals(TelemetryStatus.STARTING, presenter.state.value.status)
    }

    @Test
    fun `keeps the latest reading of every vehicle sorted by id`() = runTest {
        // Given
        val presenter = presenter(
            backgroundScope,
            finite(reading("TRK-002", speedKph = 30.0), reading("TRK-001", speedKph = 40.0), reading("TRK-001", speedKph = 55.0))
        )

        // When
        presenter.start()
        val state = presenter.state.first { it.status == TelemetryStatus.STOPPED }

        // Then
        assertEquals(listOf("TRK-001" to 55.0, "TRK-002" to 30.0), state.vehicles.map { it.vehicleId to it.speedKph })
    }

    @Test
    fun `numbers the log entries in arrival order`() = runTest {
        // Given
        val presenter = presenter(backgroundScope, finite(reading("TRK-001"), reading("TRK-002"), reading("TRK-001")))

        // When
        presenter.start()
        val state = presenter.state.first { it.status == TelemetryStatus.STOPPED }

        // Then
        assertEquals(listOf(1L, 2L, 3L), state.events.filterIsInstance<FleetEventUi.Reading>().map { it.sequence })
    }

    @Test
    fun `records a delay alert when a vehicle slows down`() = runTest {
        // Given
        val presenter = presenter(backgroundScope, finite(reading("TRK-001", speedKph = 60.0), reading("TRK-001", speedKph = 0.0)))

        // When
        presenter.start()
        val state = presenter.state.first { it.status == TelemetryStatus.STOPPED }

        // Then
        assertEquals(listOf("TRK-001" to 0.0), state.events.filterIsInstance<FleetEventUi.DelayAlert>().map { it.vehicleId to it.speedKph })
    }

    @Test
    fun `raises one alert per delay not one per slow reading`() = runTest {
        // Given
        val presenter = presenter(
            backgroundScope,
            finite(reading("TRK-001", 0.0), reading("TRK-001", 0.0), reading("TRK-001", 1.0))
        )

        // When
        presenter.start()
        val state = presenter.state.first { it.status == TelemetryStatus.STOPPED }

        // Then
        assertEquals(1, state.events.count { it is FleetEventUi.DelayAlert })
    }

    @Test
    fun `becomes stopped by itself when the stream completes`() = runTest {
        // Given
        val presenter = presenter(backgroundScope, finite(reading("TRK-001")))

        // When
        presenter.start()
        val state = presenter.state.first { it.status == TelemetryStatus.STOPPED }

        // Then
        assertEquals(1L, state.eventCount)
    }

    @Test
    fun `stop ends a never-ending stream`() = runTest {
        // Given
        val presenter = presenter(backgroundScope, FakeTelemetryRepository { endlessReadings(10, reading("TRK-001")) })
        presenter.start()
        presenter.state.first { it.eventCount >= 2 }

        // When
        presenter.stop()

        // Then
        assertEquals(TelemetryStatus.STOPPED, presenter.state.value.status)
    }

    @Test
    fun `receives nothing after stop`() = runTest {
        // Given
        val presenter = presenter(backgroundScope, FakeTelemetryRepository { endlessReadings(10, reading("TRK-001")) })
        presenter.start()
        presenter.state.first { it.eventCount >= 2 }
        presenter.stop()
        val countAtStop = presenter.state.value.eventCount

        // When
        delay(100)

        // Then
        assertEquals(countAtStop, presenter.state.value.eventCount)
    }

    @Test
    fun `starting twice does not open a second stream`() = runTest {
        // Given
        val repository = FakeTelemetryRepository { endlessReadings(10, reading("TRK-001")) }
        val presenter = presenter(backgroundScope, repository)
        presenter.start()
        presenter.state.first { it.status == TelemetryStatus.STREAMING }
        val requestsAfterFirstStart = repository.streamRequests

        // When
        presenter.start()

        // Then
        assertEquals(requestsAfterFirstStart, repository.streamRequests)
    }

    @Test
    fun `can be started again after it was stopped`() = runTest {
        // Given
        val presenter = presenter(backgroundScope, FakeTelemetryRepository { endlessReadings(10, reading("TRK-001")) })
        presenter.start()
        presenter.state.first { it.eventCount >= 1 }
        presenter.stop()

        // When
        presenter.start()
        val state = presenter.state.first { it.status == TelemetryStatus.STREAMING && it.eventCount >= 1 }

        // Then
        assertEquals(TelemetryStatus.STREAMING, state.status)
        presenter.stop()
    }

    @Test
    fun `keeps only the most recent log entries`() = runTest {
        // Given
        val readings = List(8) { reading("TRK-001", speedKph = 50.0 + it) }
        val presenter = presenter(backgroundScope, finite(*readings.toTypedArray()), maxLogEntries = 3)

        // When
        presenter.start()
        val state = presenter.state.first { it.status == TelemetryStatus.STOPPED }

        // Then
        assertEquals(listOf(6L, 7L, 8L), state.events.map { it.sequence })
    }

    @Test
    fun `fails when there is no vehicle to track`() = runTest {
        // Given
        val presenter = presenter(backgroundScope, finite(), vehicles = FakeVehicleRepository(emptyList()))

        // When
        presenter.start()
        val state = presenter.state.first { it.status == TelemetryStatus.FAILED }

        // Then
        assertEquals(UiError.NotFound("No vehicles are available to track."), state.error)
    }

    @Test
    fun `fails and stops listening when the stream breaks`() = runTest {
        // Given
        val broken = FakeTelemetryRepository { flow { throw IllegalStateException("link lost") } }
        val presenter = presenter(backgroundScope, broken)

        // When
        presenter.start()
        val state = presenter.state.first { it.status == TelemetryStatus.FAILED }

        // Then
        assertEquals(UiError.Unexpected("link lost"), state.error)
    }

    @Test
    fun `rejects a non positive log size`() = runTest {
        // Given / When / Then
        assertFailsWith<IllegalArgumentException> { presenter(backgroundScope, finite(), maxLogEntries = 0) }
    }
}
