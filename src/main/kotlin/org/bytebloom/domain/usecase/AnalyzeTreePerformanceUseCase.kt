package org.bytebloom.domain.usecase

import org.bytebloom.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.domain.performance.TreePerformanceReport
import org.bytebloom.domain.performance.TreeSearchResult
import org.bytebloom.domain.tree.binary.AVLTree
import org.bytebloom.domain.tree.binary.BST

class AnalyzeTreePerformanceUseCase(
    private val trackingIdGenerator: PackageTrackingIdGenerator,
    private val binarySearchTree: BST<String>,
    private val avlTree: AVLTree<String>
) {

    operator fun invoke(
        packageCount: Int,
        targetTrackingIds: List<String>
    ): TreePerformanceReport {

        val trackingIds =
            trackingIdGenerator.generate(packageCount)

        trackingIds.forEach {
            binarySearchTree.insert(it)
            avlTree.insert(it)
        }

        val results =
            targetTrackingIds.map { trackingId ->
                TreeSearchResult(
                    trackingId = trackingId,
                    binarySearchTreeSteps =
                        binarySearchTree.search(trackingId),
                    avlTreeSteps =
                        avlTree.search(trackingId)
                )
            }

        return TreePerformanceReport(
            totalPackages = packageCount,
            results = results
        )
    }
}