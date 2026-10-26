package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.bytebloom.logiroute.domain.repository.TelemetrySearchRepository
import org.bytebloom.logiroute.domain.usecase.telemetry.SearchTelemetryUseCase
import org.bytebloom.logiroute.presentation.SearchTraceSummary
import org.bytebloom.logiroute.presentation.TracingTelemetrySearchRepository
import org.bytebloom.logiroute.presentation.screens.TelemetrySearchScreen
import org.bytebloom.logiroute.presentation.simulatedOperatorTyping
import org.bytebloom.logiroute.presentation.toUserMessage
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.TimeSource

class TelemetrySearchPresenter(
    private val telemetrySearchRepository: TelemetrySearchRepository,
    private val searchTelemetryFor: (TelemetrySearchRepository) -> SearchTelemetryUseCase,
    view: TelemetrySearchScreen,
    parentScope: CoroutineScope,
    private val operatorTyping: () -> Flow<String> = ::simulatedOperatorTyping,
    private val timeSource: TimeSource = TimeSource.Monotonic
) : BasePresenter<TelemetrySearchScreen>(view, parentScope) {

    /** Plays the simulated typing through the debounced search and reports every step to the view. */
    fun onRunDemo(): Job = scope.launch {
        try {
            runDemo()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (@Suppress("TooGenericExceptionCaught") failure: Exception) {
            view.showErrorMessage(failure.toUserMessage())
        }
    }

    private suspend fun runDemo() {
        val startMark = timeSource.markNow()
        val counters = SearchCounters()
        fun elapsedMillis() = startMark.elapsedNow().inWholeMilliseconds

        val tracedRepository = TracingTelemetrySearchRepository(
            delegate = telemetrySearchRepository,
            onSearchStarted = { query ->
                counters.started.incrementAndGet()
                view.showSearchStarted(query, elapsedMillis())
            },
            onSearchCancelled = { query ->
                counters.cancelled.incrementAndGet()
                view.showSearchCancelled(query, elapsedMillis())
            }
        )

        val typedQueries = operatorTyping().onEach { query ->
            counters.keystrokes.incrementAndGet()
            view.showKeystroke(query, elapsedMillis())
        }

        searchTelemetryFor(tracedRepository)(typedQueries).collect { result ->
            counters.completed.incrementAndGet()
            view.showSearchResult(result, elapsedMillis())
        }

        view.showSummary(counters.toSummary())
    }

    private class SearchCounters {
        val keystrokes = AtomicInteger()
        val started = AtomicInteger()
        val cancelled = AtomicInteger()
        val completed = AtomicInteger()

        fun toSummary() = SearchTraceSummary(
            keystrokes = keystrokes.get(),
            searchesStarted = started.get(),
            searchesCancelled = cancelled.get(),
            searchesCompleted = completed.get()
        )
    }
}