package di

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.di.useCaseModule
import org.bytebloom.di.validatorModule
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.usecase.crud.packages.GetPackageByIdUseCase
import org.bytebloom.presentation.CrudUseCaseRunner
import org.bytebloom.presentation.presentationModule
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

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                validatorModule,
                useCaseModule,
                presentationModule,
                fakeRepositoryModule
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
    fun `resolves crud runner with mocked repositories`() {
        // Given
        val koin = getKoin()

        // When
        val runner = koin.get<CrudUseCaseRunner>()

        // Then
        assertNotNull(runner)
    }
}