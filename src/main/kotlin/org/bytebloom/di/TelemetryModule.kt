package org.bytebloom.di

import org.bytebloom.data.local.telemetry.SimulatedTelemetryDataSource
import org.bytebloom.data.repository.telemetry.TelemetryRepositoryImpl
import org.bytebloom.data.source.telemetry.TelemetryDataSource
import org.bytebloom.domain.repository.TelemetryRepository
import org.koin.dsl.module

val telemetryModule = module {
    single<TelemetryDataSource> {
        SimulatedTelemetryDataSource()
    }

    single<TelemetryRepository> {
        TelemetryRepositoryImpl(telemetryDataSource = get())
    }
}