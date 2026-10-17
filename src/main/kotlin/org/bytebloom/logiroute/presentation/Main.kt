package org.bytebloom.logiroute.presentation

import org.bytebloom.logiroute.di.localDataModule
import org.bytebloom.logiroute.di.networkModule
import org.bytebloom.logiroute.di.repositoryModule
import org.bytebloom.logiroute.di.telemetryModule
import org.bytebloom.logiroute.di.useCaseModule
import org.bytebloom.logiroute.di.validatorModule
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
