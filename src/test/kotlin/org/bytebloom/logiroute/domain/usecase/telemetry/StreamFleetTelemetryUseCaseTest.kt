package org.bytebloom.logiroute.domain.usecase.telemetry

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.presentation.FakeTelemetryRepository
import org.bytebloom.logiroute.presentation.reading
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StreamFleetTelemetryUseCaseTest {

    @Test
    fun `passes through readings of the requested vehicles`() = runTest {
        // Given
        val repository = FakeTelemetryRepository { flowOf(reading("TRK-001"), reading("TRK-002")) }
        val useCase = StreamFleetTelemetryUseCase(repository)

        // When
        val readings = useCase(setOf("TRK-001", "TRK-002")).toList()

        // Then
        assertEquals(listOf("TRK-001", "TRK-002"), readings.map { it.vehicleId })
    }

    @Test
    fun `drops readings of vehicles that were not requested`() = runTest {
        // Given
        val repository = FakeTelemetryRepository { flowOf(reading("TRK-001"), reading("TRK-999")) }
        val useCase = StreamFleetTelemetryUseCase(repository)

        // When
        val readings = useCase(setOf("TRK-001")).toList()

        // Then
        assertEquals(listOf("TRK-001"), readings.map { it.vehicleId })
    }

    @Test
    fun `emits nothing and never asks the repository when no vehicle is requested`() = runTest {
        // Given
        val repository = FakeTelemetryRepository { flowOf(reading("TRK-001")) }
        val useCase = StreamFleetTelemetryUseCase(repository)

        // When
        useCase(emptySet()).toList()

        // Then
        assertEquals(0, repository.streamRequests)
    }

    @Test
    fun `every collection receives the readings again because the flow is cold`() = runTest {
        // Given
        val repository = FakeTelemetryRepository { flowOf(reading("TRK-001"), reading("TRK-002")) }
        val stream = StreamFleetTelemetryUseCase(repository)(setOf("TRK-001", "TRK-002"))

        // When
        val firstCollection = stream.toList()
        val secondCollection = stream.toList()

        // Then
        assertEquals(firstCollection, secondCollection)
    }
}
