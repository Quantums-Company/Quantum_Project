package org.bytebloom.domain.tree.hierarchicalHub

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.queries.GetWarehouseLoadFactorUseCase

class HubTreeBuilder(
    private val getWarehouseLoadFactor: GetWarehouseLoadFactorUseCase
) {

    fun build(warehouses: List<Warehouse>): HubTree {

        val resolver = createResolver()

        val globalWarehouse =
            resolver.resolveGlobal(warehouses)

        val regionalWarehouses =
            resolver.resolveRegionals(
                warehouses,
                globalWarehouse
            )

        val root =
            createRoot(globalWarehouse)

        val regionalNodes =
            createRegionalNodes(
                regionalWarehouses
            )

        attachRegionals(
            root,
            regionalNodes
        )

        attachLocals(
            warehouses,
            globalWarehouse,
            regionalWarehouses,
            regionalNodes
        )

        return HubTree(root)
    }

    private fun createResolver(): HubTypeResolver =
        HubTypeResolver(
            loadFactorCalculator = getWarehouseLoadFactor::invoke
        )

    private fun createRoot(
        warehouse: Warehouse
    ): HubTreeNode =
        HubTreeNode(
            warehouse = warehouse,
            type = HubType.GLOBAL
        )

    private fun createRegionalNodes(
        warehouses: List<Warehouse>
    ): List<HubTreeNode> =
        warehouses.map { warehouse ->
            HubTreeNode(
                warehouse = warehouse,
                type = HubType.REGIONAL
            )
        }

    private fun attachRegionals(
        root: HubTreeNode,
        regionalNodes: List<HubTreeNode>
    ) {
        regionalNodes.forEach(root::addChild)
    }

    private fun attachLocals(
        warehouses: List<Warehouse>,
        globalWarehouse: Warehouse,
        regionalWarehouses: List<Warehouse>,
        regionalNodes: List<HubTreeNode>
    ) {
        val regionalIds =
            regionalWarehouses
                .map { it.id }
                .toSet()

        val regionalNodesByZone =
            regionalNodes.associateBy {
                it.warehouse.regionalZone
                    .trim()
                    .uppercase()
            }

        warehouses
            .asSequence()
            .filterNot {
                it.id.equals(
                    globalWarehouse.id,
                    ignoreCase = true
                )
            }
            .filterNot { it.id in regionalIds }
            .forEach { localWarehouse ->
                attachLocalWarehouse(
                    localWarehouse,
                    regionalNodesByZone
                )
            }
    }

    private fun attachLocalWarehouse(
        warehouse: Warehouse,
        regionalNodesByZone: Map<String, HubTreeNode>
    ) {
        val zone =
            warehouse.regionalZone
                .trim()
                .uppercase()

        regionalNodesByZone[zone]
            ?.addChild(
                HubTreeNode(
                    warehouse = warehouse,
                    type = HubType.LOCAL
                )
            )
    }
}