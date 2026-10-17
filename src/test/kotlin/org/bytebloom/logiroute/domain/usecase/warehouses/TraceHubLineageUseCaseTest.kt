package org.bytebloom.logiroute.domain.usecase.warehouses

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.tree.hub.HubTree
import org.bytebloom.logiroute.domain.tree.hub.HubTreeNode
import org.bytebloom.logiroute.domain.tree.hub.HubType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TraceHubLineageUseCaseTest {

    private val globalWarehouse = createWarehouse(
        id = "WH-001",
        name = "Global",
        regionalZone = "World",
        longitude = 0.0,
        latitude = 0.0
    )
    private val regionalWarehouse = createWarehouse(
        id = "WH-002",
        name = "Regional",
        longitude = 34.0,
        latitude = 31.0
    )
    private val localWarehouse = createWarehouse(
        id = "WH-003",
        name = "Local",
        longitude = 34.1,
        latitude = 31.1
    )
    private val unrelatedWarehouse = createWarehouse(
        id = "WH-999",
        name = "Outsider",
        regionalZone = "Nowhere",
        longitude = 1.0,
        latitude = 1.0
    )

    private lateinit var useCase: TraceHubLineageUseCase

    @BeforeEach
    fun setUp() {
        val globalNode = HubTreeNode(globalWarehouse, HubType.GLOBAL)
        val regionalNode = HubTreeNode(regionalWarehouse, HubType.REGIONAL)
        val localNode = HubTreeNode(localWarehouse, HubType.LOCAL)

        globalNode.addChild(regionalNode)
        regionalNode.addChild(localNode)

        useCase = TraceHubLineageUseCase(HubTree(globalNode))
    }

    @Test
    fun `should walk all the way up to the global root when warehouse is a leaf`() {
        // When
        val lineage = useCase(localWarehouse)

        // Then
        assertThat(lineage)
            .containsExactly(localWarehouse, regionalWarehouse, globalWarehouse)
            .inOrder()
    }

    @Test
    fun `should return only itself when warehouse is the global root`() {
        // When
        val lineage = useCase(globalWarehouse)

        // Then
        assertThat(lineage).containsExactly(globalWarehouse)
    }

    @Test
    fun `should include only itself and the global root when warehouse is regional`() {
        // When
        val lineage = useCase(regionalWarehouse)

        // Then
        assertThat(lineage)
            .containsExactly(regionalWarehouse, globalWarehouse)
            .inOrder()
    }

    @Test
    fun `should return empty lineage when warehouse is not present in the tree`() {
        // When
        val lineage = useCase(unrelatedWarehouse)

        // Then
        assertThat(lineage).isEmpty()
    }

    private fun createWarehouse(
        id: String = "WH-001",
        name: String = "Main Warehouse",
        regionalZone: String = "North",
        longitude: Double = 35.0,
        latitude: Double = 32.0
    ) = Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )
}
