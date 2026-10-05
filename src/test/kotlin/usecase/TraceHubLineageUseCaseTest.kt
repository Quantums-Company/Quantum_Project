package usecase

import org.junit.jupiter.api.Test
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.tree.hierarchicalHub.HubTree
import org.bytebloom.domain.tree.hierarchicalHub.HubTreeNode
import org.bytebloom.domain.tree.hierarchicalHub.HubType
import org.bytebloom.domain.usecase.TraceHubLineageUseCase
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TraceHubLineageUseCaseTest {

    private val globalWarehouse = Warehouse(
        id = "WH-001",
        name = "Global",
        regionalZone = "World",
        longitude = 0.0,
        latitude = 0.0
    )
    private val regionalWarehouse = Warehouse(
        id = "WH-002",
        name = "Regional",
        regionalZone = "North",
        longitude = 34.0,
        latitude = 31.0
    )
    private val localWarehouse = Warehouse(
        id = "WH-003",
        name = "Local",
        regionalZone = "North",
        longitude = 34.1,
        latitude = 31.1
    )
    private val unrelatedWarehouse = Warehouse(
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
    fun `leaf warehouse lineage walks all the way up to the global root`() {
        // When
        val lineage = useCase(localWarehouse)

        // Then
        assertEquals(listOf(localWarehouse, regionalWarehouse, globalWarehouse), lineage)
    }

    @Test
    fun `global root warehouse lineage is just itself`() {
        // When
        val lineage = useCase(globalWarehouse)

        // Then
        assertEquals(listOf(globalWarehouse), lineage)
    }

    @Test
    fun `a warehouse not present anywhere in the tree returns an empty lineage`() {
        // When
        val lineage = useCase(unrelatedWarehouse)

        // Then
        assertTrue(lineage.isEmpty())
    }

    @Test
    fun `intermediate regional warehouse lineage includes only itself and the global root`() {
        // When
        val lineage = useCase(regionalWarehouse)

        // Then
        assertEquals(listOf(regionalWarehouse, globalWarehouse), lineage)
    }
}
