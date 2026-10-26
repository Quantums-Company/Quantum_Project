package org.bytebloom.logiroute.domain.usecase.telemetry

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.bytebloom.logiroute.domain.model.result.TelemetrySearchResult
import org.bytebloom.logiroute.domain.model.exception.InvalidConfigurationException
import org.bytebloom.logiroute.domain.repository.TelemetrySearchRepository
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchTelemetryUseCase(
    private val telemetrySearchRepository: TelemetrySearchRepository,
    private val debounceTimeout: Duration = DEFAULT_DEBOUNCE_TIMEOUT
) {

    init {
        debounceTimeout.takeIf { it.isPositive() }
            ?: throw InvalidConfigurationException(
                "Debounce timeout must be positive."
            )
    }

    operator fun invoke(
        queries: Flow<String>
    ): Flow<TelemetrySearchResult> =
        queries
            .map { query -> query.trim() }
            .debounce(debounceTimeout)
            .distinctUntilChanged()
            .flatMapLatest { stableQuery -> searchFor(stableQuery) }

    private fun searchFor(
        query: String
    ): Flow<TelemetrySearchResult> = flow {
        val matches = if (query.isEmpty()) {
            emptyList()
        } else {
            telemetrySearchRepository.searchByVehicleId(query)
        }

        emit(TelemetrySearchResult(query = query, matches = matches))
    }

    private companion object {
        val DEFAULT_DEBOUNCE_TIMEOUT = 300.milliseconds
    }
}