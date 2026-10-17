package org.bytebloom.logiroute.di

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.service.IdGenerator
import org.bytebloom.logiroute.domain.usecase.packages.GetPackageByIdUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class KoinTestModulesTest : KoinTest {

    private val packageRepository = mockk<PackageRepository>()

    private val fakeRepositoryModule = module {
        single<PackageRepository> { packageRepository }
        single<WarehouseRepository> { mockk() }
        single<RouteRepository> { mockk() }
        single<VehicleRepository> { mockk() }
        single<IdGenerator> { mockk() }
    }

    private val getPackageById: GetPackageByIdUseCase by inject()

    private val trackActiveFleet: TrackActiveFleetUseCase by inject()

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                validatorModule,
                useCaseModule,
                fakeRepositoryModule,
                telemetryModule
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should resolve use case from koin using a mocked repository`() = runTest {
        // Given
        coEvery {
            packageRepository.getById("PKG-001")
        } returns null

        // When
        val result = getPackageById("PKG-001")

        // Then
        assertThat(result).isNull()
        coVerify(exactly = 1) {
            packageRepository.getById("PKG-001")
        }
    }

    @Test
    fun `should resolve telemetry tracking use case from koin`() {
        // When
        val useCase = trackActiveFleet

        // Then
        assertThat(useCase).isNotNull()
    }
}