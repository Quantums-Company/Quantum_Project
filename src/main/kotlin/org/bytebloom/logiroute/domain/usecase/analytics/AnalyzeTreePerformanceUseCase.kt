package org.bytebloom.logiroute.domain.usecase.analytics

import org.bytebloom.logiroute.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.logiroute.domain.performance.TreePerformanceReport
import org.bytebloom.logiroute.domain.performance.TreeSearchResult
import org.bytebloom.logiroute.domain.tree.binary.AVLTree
import org.bytebloom.logiroute.domain.tree.binary.BST

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

        insertTrackingIds(trackingIds)

        val results = analyzeSearchResults(targetTrackingIds)

        return TreePerformanceReport(
            totalPackages = packageCount,
            results = results
        )
    }

    private fun insertTrackingIds(trackingIds: List<String>) {
        trackingIds.forEach {
            binarySearchTree.insert(it)
            avlTree.insert(it)
        }
    }

    private fun analyzeSearchResults(
        targetTrackingIds: List<String>
    ): List<TreeSearchResult> =
        targetTrackingIds.map { trackingId ->
            TreeSearchResult(
                trackingId = trackingId,
                binarySearchTreeSteps = binarySearchTree.search(trackingId),
                avlTreeSteps = avlTree.search(trackingId)
            )
        }

}