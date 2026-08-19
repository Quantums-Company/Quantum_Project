package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadPackages
import org.bytebloom.data.lookup.findWarehouse
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository

class CsvPackageRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvDirectory: String = DEFAULT_CSV_DIRECTORY
) : PackageRepository {

    override fun getAll(): List<Package> {

        return loadPackages(csvDirectory).mapNotNull { raw ->

            val origin = warehousesById.findWarehouse(
                warehouseId = raw.originWarehouseId,
                owner = "Package",
                ownerId = raw.id
            )

            val destination = warehousesById.findWarehouse(
                warehouseId = raw.destinationWarehouseId,
                owner = "Package",
                ownerId = raw.id
            )

            if (origin == null || destination == null) {
                return@mapNotNull null
            }

            raw.toDomain(
                originWarehouse = origin,
                destinationWarehouse = destination
            )
        }
    }
}