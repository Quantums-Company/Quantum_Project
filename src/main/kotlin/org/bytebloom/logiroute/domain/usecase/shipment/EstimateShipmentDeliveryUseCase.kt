package org.bytebloom.logiroute.domain.usecase.shipment

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase

class EstimateShipmentDeliveryUseCase(
    private val packageRepository: PackageRepository,
    private val routeRepository: RouteRepository,
    private val findOptimalPath: FindOptimalPathUseCase
) {

    suspend operator fun invoke(packageId: String): Double? = coroutineScope {
        // These repository reads are independent, so overlap their I/O.
        val packagesDeferred = async { packageRepository.getAll() }
        val routesDeferred = async { routeRepository.getAll() }

        val packages = packagesDeferred.await()
        val routes = routesDeferred.await()

        val packageData = packages.find { it.id.equals(packageId, ignoreCase = true) }
            ?: return@coroutineScope null

        val path = findOptimalPath(
            packageData.originWarehouse,
            packageData.destinationWarehouse
        ) ?: return@coroutineScope null

        calculateTotalDelay(path, routes)
    }

    private fun calculateTotalDelay(
        path: List<Warehouse>,
        routes: List<Route>
    ): Double? {

        val routeByCorridor =
            routes.associateBy { it.originWarehouse.id to it.destinationWarehouse.id }

        val delays =
            path.zipWithNext()
                .map { (origin, destination) -> routeByCorridor[origin.id to destination.id]?.typicalDelayMin }

        return if (delays.any { it == null }) { null } else { delays.filterNotNull().sum().toDouble() }
    }
}