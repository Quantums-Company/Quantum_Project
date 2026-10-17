package org.bytebloom.logiroute.domain.usecase.routes

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.validator.id.RouteIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFailsWith

class DeleteRouteUseCaseTest {

    private val routeRepository: RouteRepository = mockk()
    private lateinit var useCase: DeleteRouteUseCase

    private val routeId = "RT-001"

    @BeforeEach
    fun setUp() {
        useCase = DeleteRouteUseCase(routeRepository, RouteIdValidator())
    }

    @Test
    fun `should return true when route is deleted`() = runTest {
        // Given
        coEvery { routeRepository.delete(routeId) } returns true

        // When
        val isDeleted = useCase(routeId)

        // Then
        assertThat(isDeleted).isTrue()
    }

    @Test
    fun `should call repository delete exactly once when route is deleted`() = runTest {
        // Given
        coEvery { routeRepository.delete(routeId) } returns true

        // When
        useCase(routeId)

        // Then
        coVerify(exactly = 1) { routeRepository.delete(routeId) }
    }

    @Test
    fun `should return false when repository could not delete route`() = runTest {
        // Given
        coEvery { routeRepository.delete(routeId) } returns false

        // When
        val isDeleted = useCase(routeId)

        // Then
        assertThat(isDeleted).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "WH-001", "RT-1"])
    fun `should throw validation exception when id is invalid`(invalidId: String) = runTest {
        // When
        val action = suspend { useCase(invalidId) }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "WH-001", "RT-1"])
    fun `should not call repository when id is invalid`(invalidId: String) = runTest {
        // When
        runCatching { useCase(invalidId) }

        // Then
        coVerify(exactly = 0) { routeRepository.delete(any()) }
    }

    @Test
    fun `should propagate network exception when repository is unreachable`() = runTest {
        // Given
        coEvery { routeRepository.delete(routeId) } throws NetworkUnavailableException()

        // When
        val action = suspend { useCase(routeId) }

        // Then
        assertFailsWith<NetworkUnavailableException> { action() }
    }
}
