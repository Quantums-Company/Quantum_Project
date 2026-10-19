package org.bytebloom.logiroute.ui

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
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.ConsoleTerminal
import org.bytebloom.logiroute.ui.terminal.TerminalApp
import org.bytebloom.logiroute.ui.terminal.buildTerminalMenu
import org.koin.core.context.startKoin

/**
 * `false` = only the screens required by Task 5.
 * Change to `true` to open the whole application (every management and planning screen).
 */
private const val SHOW_ALL_SCREENS = false

/**
 * Composition root of the TERMINAL application.
 * An Android app would have its own composition root (Application + ViewModels) that builds
 * the very same presenters and hands them Fragments / Compose screens as their view.
 */
fun main() {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val style = ConsoleStyle.detect()
    val terminal = ConsoleTerminal(style = style)

    val koinApp = startKoin {
        modules(
            networkModule,
            localDataModule,
            telemetryModule,
            repositoryModule,
            validatorModule,
            useCaseModule,
            presentationModule(terminal, style, appScope)
        )
    }

    try {
        runBlocking {
            TerminalApp(terminal, style, buildTerminalMenu(koinApp.koin, SHOW_ALL_SCREENS)).run()
        }
    } finally {
        // Cancel every presenter's work and wait until it has really finished.
        runBlocking { appScope.coroutineContext.job.cancelAndJoin() }
        terminal.close()
        koinApp.close()
    }
}
