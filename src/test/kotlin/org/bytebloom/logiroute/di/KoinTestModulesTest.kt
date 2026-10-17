package org.bytebloom.logiroute.di

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.di.useCaseModule
import org.bytebloom.logiroute.di.validatorModule
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.service.IdGenerator
import org.bytebloom.logiroute.domain.usecase.crud.packages.GetPackageByIdUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.bytebloom.logiroute.di.telemetryModule
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase

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
    fun `resolves use case from koin using a mocked repository`() = runTest {
        // Given
        coEvery {
            packageRepository.getById("PKG-001")
        } returns null

        // When
        val result = getPackageById("PKG-001")

        // Then
        assertNull(result)

        coVerify(exactly = 1) {
            packageRepository.getById("PKG-001")
        }
    }

    @Test
    fun `resolves telemetry tracking use case from koin`() {
        // When
        val useCase = trackActiveFleet

        // Then
        assertNotNull(useCase)
    }
}