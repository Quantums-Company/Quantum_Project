package org.bytebloom.presentation

import org.bytebloom.di.localDataModule
import org.bytebloom.di.networkModule
import org.bytebloom.di.repositoryModule
import org.bytebloom.di.telemetryModule
import org.bytebloom.di.useCaseModule
import org.bytebloom.di.validatorModule
import org.koin.core.context.startKoin

fun main() {
    val koinApp = startKoin {
        modules(
            networkModule,
            localDataModule,
            telemetryModule,
            repositoryModule,
            validatorModule,
            useCaseModule
        )
    }

    try {
        CliShell().start()
    } finally {
        koinApp.close()
    }
}
