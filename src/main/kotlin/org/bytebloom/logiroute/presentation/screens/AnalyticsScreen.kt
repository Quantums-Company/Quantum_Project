package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.performance.TreePerformanceReport

interface AnalyticsScreen : BaseScreen {
    fun showReport(report: TreePerformanceReport)
}
