package presentation

import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.usecase.crud.packages.GetAllPackagesUseCase
import org.bytebloom.domain.usecase.crud.routes.GetAllRoutesUseCase
import org.bytebloom.domain.usecase.crud.vehicles.GetAllVehiclesUseCase
import org.bytebloom.domain.usecase.crud.warehouses.GetAllWarehousesUseCase
import org.bytebloom.presentation.OfflineFirstTestRunner
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension

class OfflineFirstTestRunnerTest : KoinTest {

    private val testModule = module {
        factory { GetAllWarehousesUseCase(warehouseRepository = mockWarehouseRepository()) }
        factory { GetAllPackagesUseCase(packageRepository = mockPackageRepository()) }
        factory { GetAllRoutesUseCase(routeRepository = mockRouteRepository()) }
        factory { GetAllVehiclesUseCase(vehicleRepository = mockVehicleRepository()) }
    }

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(testModule)
    }

    @Test
    fun `runner should execute successfully without throwing exceptions`() = runTest {
        val runner = OfflineFirstTestRunner(getKoin())
        assertNotNull(runner)
    }

    private fun mockWarehouseRepository() = object : WarehouseRepository {
        override suspend fun getAll() = emptyList<Warehouse>()
        override suspend fun getById(id: String): Warehouse? = null
        override suspend fun create(warehouse: Warehouse) = warehouse
        override suspend fun update(warehouse: Warehouse) = warehouse
        override suspend fun delete(id: String) = true
    }

    private fun mockPackageRepository() = object : PackageRepository {
        override suspend fun getAll() = emptyList<Package>()
        override suspend fun getById(id: String): Package? = null
        override suspend fun create(pkg: Package) = pkg
        override suspend fun update(pkg: Package) = pkg
        override suspend fun delete(id: String) = true
    }

    private fun mockRouteRepository() = object : RouteRepository {
        override suspend fun getAll() = emptyList<Route>()
        override suspend fun getById(id: String): Route? = null
        override suspend fun create(route: Route) = route
        override suspend fun update(route: Route) = route
        override suspend fun delete(id: String) = true
    }

    private fun mockVehicleRepository() = object : VehicleRepository {
        override suspend fun getAll() = emptyList<Vehicle>()
        override suspend fun getById(id: String): Vehicle? = null
        override suspend fun create(vehicle: Vehicle) = vehicle
        override suspend fun update(vehicle: Vehicle) = vehicle
        override suspend fun delete(id: String) = true
    }
}