package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import org.bytebloom.logiroute.di.localDataModule
import org.bytebloom.logiroute.di.networkModule
import org.bytebloom.logiroute.di.presentationModule
import org.bytebloom.logiroute.di.repositoryModule
import org.bytebloom.logiroute.di.telemetryModule
import org.bytebloom.logiroute.di.useCaseModule
import org.bytebloom.logiroute.di.validatorModule
import org.bytebloom.logiroute.presentation.presenter.AppPresenters
import org.bytebloom.logiroute.presentation.ui.console.ConsoleApp
import org.bytebloom.logiroute.presentation.ui.console.ConsoleStyle
import org.bytebloom.logiroute.presentation.ui.console.ConsoleTerminal
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf

/**
 * Composition root of the CONSOLE application.
 * An Android app would have its own composition root (an Activity + ViewModels)
 * that builds the very same [AppPresenters] and hands them to Compose screens instead.
 */
fun main() {
    val koinApp = startKoin {
        modules(
            networkModule,
            localDataModule,
            telemetryModule,
            repositoryModule,
            validatorModule,
            useCaseModule,
            presentationModule
        )
    }

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val style = ConsoleStyle.detect()
    val terminal = ConsoleTerminal(style = style)

    try {
        runBlocking {
            val presenters = koinApp.koin.get<AppPresenters> { parametersOf(appScope) }
            ConsoleApp(terminal, style, presenters).run()
        }
    } finally {
        // Cancel every presenter's work and wait until it has really finished.
        runBlocking { appScope.coroutineContext.job.cancelAndJoin() }
        terminal.close()
        koinApp.close()
    }
}
