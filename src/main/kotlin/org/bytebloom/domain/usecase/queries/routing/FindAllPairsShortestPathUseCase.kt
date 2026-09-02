package org.bytebloom.domain.usecase.queries.routing

import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.model.Warehouse

class FindAllPairsShortestPathUseCase(
    private val routeRepository: RouteRepository
) {
    operator fun invoke(warehouses: List<Warehouse>): Map<Warehouse, Map<Warehouse, Double>> {
        val warehouseIndex = createWarehouseIndex(warehouses)
        val distances = initializeDistances(warehouses.size)
        addDirectRoutes(
            distances,
            warehouseIndex
        )

        calculateShortestPaths(
            distances,
            warehouses
        )

        return buildResult(
            warehouses,
            warehouseIndex,
            distances
        )

    }

    private fun createWarehouseIndex(
        warehouses: List<Warehouse>
    ): Map<String, Int> {
        return warehouses.mapIndexed { index, warehouse ->
            warehouse.id to index
        }.toMap()
    }

    private fun initializeDistances(
        warehousesCount: Int
    ): MutableList<MutableList<Double>> {

        val distances = MutableList(warehousesCount) {
            MutableList(warehousesCount) {
                Double.POSITIVE_INFINITY
            }
        }
        distances.forEachIndexed { index, _ ->
            distances[index][index] = 0.0
        }
        return distances
    }

    private fun addDirectRoutes(
        distances: MutableList<MutableList<Double>>,
        warehouseIndex: Map<String, Int>
    ) {
        val routes = routeRepository.getAll()
        routes.forEach { route ->
            val originIndex = warehouseIndex[route.originWarehouse.id]!!
            val destinationIndex= warehouseIndex[route.destinationWarehouse.id]!!
            distances[originIndex][destinationIndex] = route.distanceKm
        }

    }

    private fun calculateShortestPaths(
        distances: MutableList<MutableList<Double>>,
        warehouses: List<Warehouse>
        ) {
        warehouses.indices.forEach { intermediateWarehouse ->
            warehouses.indices.forEach { originWarehouse ->
                warehouses.indices.forEach { destinationWarehouse ->
                    distances[originWarehouse][destinationWarehouse] = minOf(
                        distances[originWarehouse][destinationWarehouse],
                        distances[originWarehouse][intermediateWarehouse] +
                                distances[intermediateWarehouse][destinationWarehouse]

                    )
                }
            }
        }
    }


    private fun buildResult(
        warehouses: List<Warehouse>,
        warehouseIndex: Map<String, Int>,
        distances: List<List<Double>>
    ): Map<Warehouse, Map<Warehouse, Double>> {
        return warehouses.associate { originWarehouse ->
            originWarehouse to warehouses.associate {destinationWarehouse ->
                destinationWarehouse to distances[
                    warehouseIndex[originWarehouse.id]!!
                ][
                    warehouseIndex[destinationWarehouse.id]!!
                ]
            }
        }
    }


}