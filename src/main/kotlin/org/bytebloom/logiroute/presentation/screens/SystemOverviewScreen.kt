package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.presentation.SystemSummary

/** What the system overview screen must be able to show. Implemented by the UI. */
interface SystemOverviewScreen : BaseScreen {

    suspend fun showSummary(summary: SystemSummary)
}
