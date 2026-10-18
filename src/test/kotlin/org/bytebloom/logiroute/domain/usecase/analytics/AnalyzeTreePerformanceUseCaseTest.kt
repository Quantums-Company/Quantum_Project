package org.bytebloom.logiroute.domain.usecase.analytics

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.logiroute.domain.tree.binary.AVLTree
import org.bytebloom.logiroute.domain.tree.binary.BST
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AnalyzeTreePerformanceUseCaseTest {

    private lateinit var useCase: AnalyzeTreePerformanceUseCase

    private val firstTrackingId = "PKG-000001"
    private val secondTrackingId = "PKG-000002"
    private val thirdTrackingId = "PKG-000003"
    private val neverInsertedTrackingId = "PKG-999999"

    @BeforeEach
    fun setUp() {
        useCase = AnalyzeTreePerformanceUseCase(
            trackingIdGenerator = PackageTrackingIdGenerator(),
            binarySearchTree = BST(),
            avlTree = AVLTree()
        )
    }

    @Test
    fun `should report the requested total package count`() {
        // Given
        val packageCount = 3

        // When
        val report = useCase(packageCount = packageCount, targetTrackingIds = listOf(firstTrackingId))

        // Then
        assertThat(report.totalPackages).isEqualTo(packageCount)
    }

    @Test
    fun `should return one search result per requested tracking id in the order given`() {
        // Given
        val targetIds = listOf(secondTrackingId, firstTrackingId)

        // When
        val report = useCase(packageCount = 3, targetTrackingIds = targetIds)

        // Then
        assertThat(report.results.map { it.trackingId }).containsExactlyElementsIn(targetIds).inOrder()
    }

    @Test
    fun `should have avl worst case lookup no worse than bst when ids are inserted in ascending order`() {
        // Given
        val targetIds = listOf(firstTrackingId, secondTrackingId, thirdTrackingId)

        // When
        val report = useCase(packageCount = 3, targetTrackingIds = targetIds)

        // Then
        val maxBstSteps = report.results.maxOf { it.binarySearchTreeSteps }
        val maxAvlSteps = report.results.maxOf { it.avlTreeSteps }
        assertThat(maxAvlSteps).isAtMost(maxBstSteps)
    }

    @Test
    fun `should need as many bst steps as packages for the last id when ids are inserted in ascending order`() {
        // Given
        val packageCount = 3
        val targetIds = listOf(firstTrackingId, secondTrackingId, thirdTrackingId)

        // When
        val report = useCase(packageCount = packageCount, targetTrackingIds = targetIds)

        // Then
        val lastResult = report.results.last { it.trackingId == thirdTrackingId }
        assertThat(lastResult.binarySearchTreeSteps).isEqualTo(packageCount)
    }

    @Test
    fun `should keep every avl lookup within balanced tree height when ids are inserted in ascending order`() {
        // Given
        val balancedTreeHeightForThreeNodes = 2
        val targetIds = listOf(firstTrackingId, secondTrackingId, thirdTrackingId)

        // When
        val report = useCase(packageCount = 3, targetTrackingIds = targetIds)

        // Then
        report.results.forEach { result ->
            assertThat(result.avlTreeSteps).isAtMost(balancedTreeHeightForThreeNodes)
        }
    }

    @Test
    fun `should return a step count when tracking id was never inserted`() {
        // When
        val report = useCase(packageCount = 2, targetTrackingIds = listOf(neverInsertedTrackingId))

        // Then
        assertThat(report.results).hasSize(1)
        assertThat(report.results.first().binarySearchTreeSteps).isGreaterThan(0)
        assertThat(report.results.first().avlTreeSteps).isGreaterThan(0)
    }

    @Test
    fun `should return zero steps when no packages were inserted`() {
        // When
        val report = useCase(packageCount = 0, targetTrackingIds = listOf(firstTrackingId))

        // Then
        assertThat(report.totalPackages).isEqualTo(0)
        assertThat(report.results.first().binarySearchTreeSteps).isEqualTo(0)
        assertThat(report.results.first().avlTreeSteps).isEqualTo(0)
    }
}
