package usecase.crud.route

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.NetworkUnavailableException
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.usecase.crud.route.DeleteRouteUseCase
import org.bytebloom.domain.validator.id.RouteIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFailsWith

class DeleteRouteUseCaseTest {

    private lateinit var routeRepository: RouteRepository
    private lateinit var deleteRoute: DeleteRouteUseCase

    @BeforeEach
    fun setUp() {
        routeRepository = mockk()
        deleteRoute = DeleteRouteUseCase(routeRepository, RouteIdValidator())
    }

    @Test
    fun `should return true when route is deleted`() = runTest {
        // Given
        coEvery { routeRepository.delete(ROUTE_ID) } returns true

        // When
        val isDeleted = deleteRoute(ROUTE_ID)

        // Then
        assertThat(isDeleted).isTrue()
        coVerify(exactly = 1) { routeRepository.delete(ROUTE_ID) }
    }

    @Test
    fun `should return false when repository could not delete route`() = runTest {
        // Given
        coEvery { routeRepository.delete(ROUTE_ID) } returns false

        // When
        val isDeleted = deleteRoute(ROUTE_ID)

        // Then
        assertThat(isDeleted).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "WH-001", "RT-1"])
    fun `should throw validation exception and skip deletion when id is invalid`(invalidId: String) = runTest {
        // Given: invalidId from @ValueSource

        // When & Then
        assertFailsWith<EntityValidationException> { deleteRoute(invalidId) }
        coVerify(exactly = 0) { routeRepository.delete(any()) }
    }

    @Test
    fun `should propagate network exception when repository is unreachable`() = runTest {
        // Given
        coEvery { routeRepository.delete(ROUTE_ID) } throws NetworkUnavailableException()

        // When & Then
        assertFailsWith<NetworkUnavailableException> { deleteRoute(ROUTE_ID) }
    }

    private companion object {
        const val ROUTE_ID = "RT-001"
    }
}