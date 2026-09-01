package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.routing.common.EmptyHubFinder

class FindNearestEmptyHubUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val packageRepository: PackageRepository,
    private val emptyHubFinder: EmptyHubFinder
) {

    operator fun invoke(
        startWarehouse: Warehouse
    ): Warehouse? {

        val warehouses = warehouseRepository.getAll()
        val packages = packageRepository.getAll()

        val emptyWarehouses =
            warehouses
                .filter { warehouse ->
                    packages.none {
                        it.originWarehouse.id ==
                                warehouse.id
                    }
                }
                .toSet()

        return emptyHubFinder.findNearestEmptyHub(
            startWarehouse,
            emptyWarehouses
        )
    }
}