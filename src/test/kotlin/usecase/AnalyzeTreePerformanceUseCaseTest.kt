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
    fun `on ascending insertion order, AVL never needs more search steps than the unbalanced BST`() {
        // Given — PKG-000001..000003 insert in sorted order, the exact case that degrades a plain BST
        val targetIds = listOf("PKG-000001", "PKG-000002", "PKG-000003")

        // When
        val report = useCase(packageCount = 3, targetTrackingIds = targetIds)

        // Then — this is the actual performance claim this use case exists to demonstrate
        report.results.forEach { result ->
            assertTrue(
                result.avlTreeSteps <= result.binarySearchTreeSteps,
                "Expected AVL (${result.avlTreeSteps}) <=" +
                        " BST (${result.binarySearchTreeSteps}) for ${result.trackingId}"
            )
        }
        // The worst-case BST lookup (the last inserted id) takes exactly 3 steps on a fully right-skewed tree
        assertEquals(
            3,
            report.results.last { it.trackingId == "PKG-000003" }.binarySearchTreeSteps
        )
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
