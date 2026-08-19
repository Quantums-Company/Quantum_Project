package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadRoutes
import org.bytebloom.data.lookup.findWarehouse
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository

class CsvRouteRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvDirectory: String = DEFAULT_CSV_DIRECTORY
) : RouteRepository {

    override fun getAll(): List<Route> {

        return loadRoutes(csvDirectory).mapNotNull { raw ->

            val origin = warehousesById.findWarehouse(
                warehouseId = raw.originWarehouseId,
                owner = "Route",
                ownerId = raw.id
            )

            val destination = warehousesById.findWarehouse(
                warehouseId = raw.destinationWarehouseId,
                owner = "Route",
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