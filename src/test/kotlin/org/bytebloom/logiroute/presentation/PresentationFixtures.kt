package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures
import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.repository.TelemetryRepository
import org.bytebloom.logiroute.domain.repository.TrainingCargoRepository
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository

/** Pretend network latency, so tests can observe the LOADING state. Virtual time under runTest. */
internal const val FAKE_LATENCY_MILLIS = 20L

internal fun warehouse(
    id: String,
    name: String = "Warehouse $id",
    zone: String = "North",
    latitude: Double = 31.9,
    longitude: Double = 35.2
) = Warehouse(id = id, name = name, regionalZone = zone, longitude = longitude, latitude = latitude)

internal fun route(id: String, from: Warehouse, to: Warehouse, distanceKm: Double) = Route(
    id = id,
    distanceKm = distanceKm,
    typicalDelayMin = 5,
    originWarehouse = from,
    destinationWarehouse = to
)

internal fun vehicle(id: String, at: Warehouse) = Vehicle(
    id = id,
    maxCapacityKg = 1_000.0,
    costPerKm = 2.5,
    currentWarehouse = at
)

internal fun parcel(id: String, from: Warehouse, to: Warehouse, weightKg: Double = 10.0) = Package(
    id = id,
    weight = weightKg,
    priority = Priority.STANDARD,
    originWarehouse = from,
    destinationWarehouse = to
)

internal fun reading(vehicleId: String, speedKph: Double = 50.0, fuelPercent: Double = 80.0) = VehicleTelemetry(
    vehicleId = vehicleId,
    latitude = 31.5,
    longitude = 35.0,
    speedKph = speedKph,
    fuelPercent = fuelPercent
)

internal fun trainingRecord(region: Region, latitude: Double, longitude: Double, weightKg: Double = 10.0) =
    LabeledCargoRecord(CargoFeatures(latitude, longitude, weightKg), region)

internal class FakeWarehouseRepository(
    private val warehouses: List<Warehouse> = emptyList(),
    private val failure: Throwable? = null
) : WarehouseRepository {
    override suspend fun getAll(): List<Warehouse> {
        delay(FAKE_LATENCY_MILLIS)
        failure?.let { throw it }
        return warehouses
    }

    override suspend fun getById(id: String) = warehouses.firstOrNull { it.id == id }
    override suspend fun create(warehouse: Warehouse) = warehouse
    override suspend fun update(warehouse: Warehouse) = warehouse
    override suspend fun delete(id: String) = true
}

internal class FakeRouteRepository(
    private val routes: List<Route> = emptyList(),
    private val failure: Throwable? = null
) : RouteRepository {
    override suspend fun getAll(): List<Route> {
        delay(FAKE_LATENCY_MILLIS)
        failure?.let { throw it }
        return routes
    }

    override suspend fun getById(id: String) = routes.firstOrNull { it.id == id }
    override suspend fun create(route: Route) = route
    override suspend fun update(route: Route) = route
    override suspend fun delete(id: String) = true
}

internal class FakeVehicleRepository(
    private val vehicles: List<Vehicle> = emptyList(),
    private val failure: Throwable? = null
) : VehicleRepository {
    override suspend fun getAll(): List<Vehicle> {
        delay(FAKE_LATENCY_MILLIS)
        failure?.let { throw it }
        return vehicles
    }

    override suspend fun getById(id: String) = vehicles.firstOrNull { it.id == id }
    override suspend fun create(vehicle: Vehicle) = vehicle
    override suspend fun update(vehicle: Vehicle) = vehicle
    override suspend fun delete(id: String) = true
}

internal class FakePackageRepository(
    private val packages: List<Package> = emptyList(),
    private val failure: Throwable? = null
) : PackageRepository {
    override suspend fun getAll(): List<Package> {
        delay(FAKE_LATENCY_MILLIS)
        failure?.let { throw it }
        return packages
    }

    override suspend fun getById(id: String) = packages.firstOrNull { it.id == id }
    override suspend fun create(pkg: Package) = pkg
    override suspend fun update(pkg: Package) = pkg
    override suspend fun delete(id: String) = true
}

internal class FakeTrainingCargoRepository(
    private val records: List<LabeledCargoRecord> = emptyList()
) : TrainingCargoRepository {
    override suspend fun getTrainingRecords(): List<LabeledCargoRecord> {
        delay(FAKE_LATENCY_MILLIS)
        return records
    }
}

internal class FakeTelemetryRepository(
    private val streamOf: (List<String>) -> Flow<VehicleTelemetry> = { emptyFlowOfReadings() }
) : TelemetryRepository {
    var streamRequests = 0
        private set

    override fun streamTelemetry(vehicleIds: List<String>): Flow<VehicleTelemetry> {
        streamRequests++
        return streamOf(vehicleIds)
    }
}

internal fun emptyFlowOfReadings(): Flow<VehicleTelemetry> = flow { }

/** Emits [readings] one by one, [everyMillis] apart. Finite: completes after the last one. */
internal fun readingsEvery(everyMillis: Long, readings: List<VehicleTelemetry>): Flow<VehicleTelemetry> = flow {
    readings.forEach {
        emit(it)
        delay(everyMillis)
    }
}

/** Emits the same reading forever, [everyMillis] apart, until the collector is cancelled. */
internal fun endlessReadings(everyMillis: Long, vararg readings: VehicleTelemetry): Flow<VehicleTelemetry> = flow {
    while (true) {
        readings.forEach { emit(it) }
        delay(everyMillis)
    }
}
