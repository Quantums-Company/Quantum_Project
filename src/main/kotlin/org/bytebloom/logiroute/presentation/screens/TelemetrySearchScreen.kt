package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.result.TelemetrySearchResult
import org.bytebloom.logiroute.presentation.SearchTraceSummary

/**
 * What the debounced telemetry search screen must be able to show. Implemented by the UI.
 * Every event carries the milliseconds elapsed since the operator started typing.
 */
interface TelemetrySearchScreen : BaseScreen {

    fun showKeystroke(query: String, elapsedMillis: Long)

    fun showSearchStarted(query: String, elapsedMillis: Long)

    fun showSearchCancelled(query: String, elapsedMillis: Long)

    fun showSearchResult(result: TelemetrySearchResult, elapsedMillis: Long)

    fun showSummary(summary: SearchTraceSummary)
}