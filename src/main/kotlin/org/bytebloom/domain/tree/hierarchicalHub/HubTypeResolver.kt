package org.bytebloom.domain.tree.hierarchicalHub

import org.bytebloom.domain.model.Warehouse
class HubTypeResolver(
    private val loadFactorCalculator: (Warehouse) -> Double
) {

    fun resolveGlobal(
        warehouses: List<Warehouse>
    ): Warehouse =
        warehouses.maxBy { loadFactorCalculator(it) }

    fun resolveRegionals(
        warehouses: List<Warehouse>,
        globalWarehouse: Warehouse
    ): List<Warehouse> =
        warehouses
            .asSequence()
            .filterNot {
                it.id.equals(
                    globalWarehouse.id,
                    ignoreCase = true
                )
            }
            .filter { it.regionalZone.isNotBlank() }
            .groupBy {
                it.regionalZone.trim().uppercase()
            }
            .values
            .map { zoneWarehouses ->
                zoneWarehouses.maxBy {
                    loadFactorCalculator(it)
                }
            }
            .toList()
}