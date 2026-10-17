package org.bytebloom.logiroute.di

import org.bytebloom.logiroute.data.local.telemetry.SimulatedTelemetryDataSource
import org.bytebloom.logiroute.data.repository.telemetry.TelemetryRepositoryImpl
import org.bytebloom.logiroute.data.source.telemetry.TelemetryDataSource
import org.bytebloom.logiroute.domain.repository.TelemetryRepository
import org.koin.dsl.module

val telemetryModule = module {
    single<TelemetryDataSource> {
        SimulatedTelemetryDataSource()
    }

    single<TelemetryRepository> {
        TelemetryRepositoryImpl(telemetryDataSource = get())
    }
}