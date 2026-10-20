package org.bytebloom.logiroute.domain.usecase.telemetry

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.repository.TelemetryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StreamFleetTelemetryUseCaseTest {

    private val telemetryRepository: TelemetryRepository = mockk()
    private lateinit var useCase: StreamFleetTelemetryUseCase

    @BeforeEach
    fun setUp() {
        useCase = StreamFleetTelemetryUseCase(telemetryRepository)
    }

    @Test
    fun `should pass through readings when vehicles are requested`() = runTest {
        // Given
        val requestedIds = setOf(FIRST_VEHICLE_ID, SECOND_VEHICLE_ID)
        every { telemetryRepository.streamTelemetry(any()) } returns
                flowOf(createReading(FIRST_VEHICLE_ID), createReading(SECOND_VEHICLE_ID))

        // When
        val readings = useCase(requestedIds).toList()

        // Then
        assertThat(readings.map { it.vehicleId })
            .containsExactly(FIRST_VEHICLE_ID, SECOND_VEHICLE_ID)
            .inOrder()
    }

    @Test
    fun `should drop readings when their vehicle was not requested`() = runTest {
        // Given
        every { telemetryRepository.streamTelemetry(any()) } returns
                flowOf(createReading(FIRST_VEHICLE_ID), createReading(UNREQUESTED_VEHICLE_ID))

        // When
        val readings = useCase(setOf(FIRST_VEHICLE_ID)).toList()

        // Then
        assertThat(readings.map { it.vehicleId }).containsExactly(FIRST_VEHICLE_ID)
    }

    @Test
    fun `should emit nothing when no vehicle is requested`() = runTest {
        // When
        val readings = useCase(emptySet()).toList()

        // Then
        assertThat(readings).isEmpty()
    }

    @Test
    fun `should not call repository when no vehicle is requested`() = runTest {
        // When
        useCase(emptySet()).toList()

        // Then
        verify(exactly = 0) { telemetryRepository.streamTelemetry(any()) }
    }

    @Test
    fun `should deliver the readings to every collector when the stream is collected twice`() = runTest {
        // Given
        every { telemetryRepository.streamTelemetry(any()) } returns
                flowOf(createReading(FIRST_VEHICLE_ID), createReading(SECOND_VEHICLE_ID))
        val stream = useCase(setOf(FIRST_VEHICLE_ID, SECOND_VEHICLE_ID))

        // When
        val collections = List(COLLECTOR_COUNT) { stream.toList() }

        // Then
        assertThat(collections.map { collection -> collection.map { it.vehicleId } })
            .containsExactly(
                listOf(FIRST_VEHICLE_ID, SECOND_VEHICLE_ID),
                listOf(FIRST_VEHICLE_ID, SECOND_VEHICLE_ID)
            )
    }

    private fun createReading(
        vehicleId: String = FIRST_VEHICLE_ID,
        speedKph: Double = 50.0,
        fuelPercent: Double = 80.0
    ) = VehicleTelemetry(
        vehicleId = vehicleId,
        latitude = 31.5,
        longitude = 35.0,
        speedKph = speedKph,
        fuelPercent = fuelPercent
    )

    private companion object {
        const val FIRST_VEHICLE_ID = "TRK-001"
        const val SECOND_VEHICLE_ID = "TRK-002"
        const val UNREQUESTED_VEHICLE_ID = "TRK-999"
        const val COLLECTOR_COUNT = 2
    }
}