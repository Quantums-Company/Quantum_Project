package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.logiroute.domain.usecase.analytics.AnalyzeTreePerformanceUseCase
import org.bytebloom.logiroute.presentation.screens.AnalyticsScreen

class AnalyticsPresenter(
    private val analyzeTreePerformance: AnalyzeTreePerformanceUseCase,
    private val trackingIds: PackageTrackingIdGenerator,
    view: AnalyticsScreen,
    parentScope: CoroutineScope,
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BasePresenter<AnalyticsScreen>(view, parentScope) {

    /** Compares search steps in a plain BST and an AVL tree holding [packageCount] tracking ids. */
    fun onAnalyze(packageCount: Int, targetIds: List<String> = emptyList()): Job = perform(
        task = {
            require(packageCount in MIN_COUNT..MAX_COUNT) {
                "Package count must be between $MIN_COUNT and $MAX_COUNT."
            }
            val targets = targetIds.ifEmpty { defaultTargets(packageCount) }
            withContext(computeDispatcher) { analyzeTreePerformance(packageCount, targets) }
        }
    ) { view.showReport(it) }

    private fun defaultTargets(count: Int): List<String> {
        val ids = trackingIds.generate(count)
        return listOf(ids.first(), ids[ids.size / 2], ids.last()).distinct()
    }

    private companion object {
        const val MIN_COUNT = 1
        const val MAX_COUNT = 100_000
    }
}
