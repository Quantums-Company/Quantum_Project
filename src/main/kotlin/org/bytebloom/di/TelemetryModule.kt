package org.bytebloom.di

import org.bytebloom.data.local.telemetry.SimulatedTelemetryStreamingService
import org.bytebloom.domain.service.TelemetryStreamingService
import org.koin.dsl.module

val telemetryModule = module {
    single<TelemetryStreamingService> {
        SimulatedTelemetryStreamingService()
    }
}