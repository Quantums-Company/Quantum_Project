package usecase

import org.junit.jupiter.api.Test
import org.bytebloom.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.domain.tree.binary.AVLTree
import org.bytebloom.domain.tree.binary.BST
import org.bytebloom.domain.usecase.AnalyzeTreePerformanceUseCase
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnalyzeTreePerformanceUseCaseTest {

    private lateinit var useCase: AnalyzeTreePerformanceUseCase

    @BeforeEach
    fun setUp() {
        useCase = AnalyzeTreePerformanceUseCase(
            trackingIdGenerator = PackageTrackingIdGenerator(),
            binarySearchTree = BST(),
            avlTree = AVLTree()
        )
    }

    @Test
    fun `reports the correct total package count`() {
        // When
        val report = useCase(packageCount = 3, targetTrackingIds = listOf("PKG-000001"))

        // Then
        assertEquals(3, report.totalPackages)
    }

    @Test
    fun `returns one search result per requested tracking id, in the order given`() {
        // When
        val report = useCase(packageCount = 3, targetTrackingIds = listOf("PKG-000002", "PKG-000001"))

        // Then
        assertEquals(listOf("PKG-000002", "PKG-000001"), report.results.map { it.trackingId })
    }

    @Test
    fun `on ascending insertion order, AVL's worst-case lookup is never worse than the unbalanced BST's`() {
        // Given — PKG-000001..000003 insert in sorted order, the exact case that degrades a plain BST
        val targetIds = listOf("PKG-000001", "PKG-000002", "PKG-000003")

        // When
        val report = useCase(packageCount = 3, targetTrackingIds = targetIds)

        // Then — AVL's balancing guarantees a better (or equal) WORST-CASE bound across the
        // whole dataset. It does NOT guarantee every individual id is faster than in the
        // unbalanced BST — a rebalance can push one element slightly deeper even as it
        // shortens the tree's overall worst case (PKG-000001 is exactly that case here:
        // 2 AVL steps vs 1 BST step, yet AVL's worst case is still better overall).
        val maxBstSteps = report.results.maxOf { it.binarySearchTreeSteps }
        val maxAvlSteps = report.results.maxOf { it.avlTreeSteps }
        assertTrue(
            maxAvlSteps <= maxBstSteps,
            "Expected AVL worst case ($maxAvlSteps) <= BST worst case ($maxBstSteps)"
        )

        // The unbalanced BST degenerates into a linked list on ascending input: the last
        // inserted id requires exactly N steps.
        assertEquals(3, report.results.last { it.trackingId == "PKG-000003" }.binarySearchTreeSteps)

        // AVL's self-balancing keeps every single lookup within ceil(log2(N+1)) steps — here, 2.
        report.results.forEach { result ->
            assertTrue(
                result.avlTreeSteps <= 2,
                "Expected AVL step count <= 2 for ${result.trackingId}, got ${result.avlTreeSteps}"
            )
        }
    }

    @Test
    fun `searching for a tracking id that was never inserted still returns a step count, not an error`() {
        // When
        val report = useCase(packageCount = 2, targetTrackingIds = listOf("PKG-999999"))

        // Then
        assertEquals(1, report.results.size)
        assertTrue(report.results.first().binarySearchTreeSteps > 0)
        assertTrue(report.results.first().avlTreeSteps > 0)
    }

    @Test
    fun `zero packages still allows searching, returning minimal step counts`() {
        // When
        val report = useCase(packageCount = 0, targetTrackingIds = listOf("PKG-000001"))

        // Then
        assertEquals(0, report.totalPackages)
        assertEquals(0, report.results.first().binarySearchTreeSteps)
        assertEquals(0, report.results.first().avlTreeSteps)
    }
}
