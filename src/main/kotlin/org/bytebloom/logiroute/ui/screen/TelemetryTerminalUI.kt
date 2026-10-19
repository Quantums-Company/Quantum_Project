package org.bytebloom.logiroute.ui.screen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.model.result.VehicleDelayNotification
import org.bytebloom.logiroute.presentation.screen.TelemetryScreen
import org.bytebloom.logiroute.presentation.presenter.TelemetryPresenter
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.padVisible
import org.bytebloom.logiroute.ui.terminal.widget.percentBar
import org.bytebloom.logiroute.ui.terminal.widget.table
import org.bytebloom.logiroute.ui.terminal.writeLines
import java.util.concurrent.atomic.AtomicInteger

/** The terminal implementation of [TelemetryScreen]. */
class TelemetryTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (TelemetryScreen) -> TelemetryPresenter
) : TerminalScreenBase(terminal, style, uiScope), TelemetryScreen {

    private val presenter by lazy { presenterFactory(this) }
    private val eventCounter = AtomicInteger()

    override suspend fun show() {
        terminal.writeLine()
        terminal.writeLine(style.bold(style.cyan("Live vehicle telemetry")) + style.dim("   press Enter to stop"))
        terminal.writeLine()

        eventCounter.set(0)
        loadingMessage = "Loading vehicles…"
        val streaming = presenter.onStart()

        // Two ways to leave: the user presses Enter, or the stream ends / fails on its own.
        coroutineScope {
            val enterPressed = async { terminal.readLine() }
            select {
                enterPressed.onAwait { }
                streaming.onJoin { }
            }
            enterPressed.cancel()
        }

        presenter.onStop()
        streaming.join() // waits until the summary has been printed
    }

    override fun showTrackingStarted(vehicleIds: Set<String>) {
        terminal.writeLine(style.dim("Tracking ${vehicleIds.size} vehicle(s): ${vehicleIds.sorted().joinToString()}"))
        terminal.writeLine()
    }

    override fun showTelemetry(telemetry: VehicleTelemetry) {
        val divider = style.dim(style.glyphs.vertical)
        terminal.writeLine(
            listOf(
                numberLabel(),
                style.bold(padVisible(telemetry.vehicleId, VEHICLE_WIDTH)),
                padVisible(formatNumber(telemetry.speedKph, 0), SPEED_WIDTH, alignRight = true) + " km/h",
                "fuel " + style.percentBar(telemetry.fuelPercent),
                style.dim(gps(telemetry))
            ).joinToString(" $divider ")
        )
    }

    override fun showDelayAlert(notification: VehicleDelayNotification) {
        val divider = style.dim(style.glyphs.vertical)
        val text = "${style.glyphs.warning} DELAY  ${notification.vehicleId} slowed to " +
                "${formatNumber(notification.speedKph, 0)} km/h"
        terminal.writeLine(listOf(numberLabel(), style.red(text)).joinToString(" $divider "))
    }

    override fun showTrackingStopped(latestReadings: List<VehicleTelemetry>) {
        terminal.writeLine()
        terminal.writeLine(
            style.green("${style.glyphs.success} Telemetry stopped.") +
                    style.dim("  ${eventCounter.get()} events received.")
        )
        if (latestReadings.isEmpty()) return

        terminal.writeLines(
            style.table(
                headers = listOf("Vehicle", "Speed (km/h)", "Fuel", "GPS"),
                rows = latestReadings.map {
                    listOf(it.vehicleId, formatNumber(it.speedKph, 0), style.percentBar(it.fuelPercent), gps(it))
                },
                rightAligned = setOf(1)
            )
        )
    }

    private fun numberLabel(): String =
        style.dim("#" + eventCounter.incrementAndGet().toString().padStart(SEQUENCE_WIDTH, '0'))

    private fun gps(t: VehicleTelemetry) =
        "${formatNumber(t.latitude, GPS_DECIMALS)}, ${formatNumber(t.longitude, GPS_DECIMALS)}"

    private companion object {
        const val SEQUENCE_WIDTH = 4
        const val VEHICLE_WIDTH = 8
        const val SPEED_WIDTH = 3
        const val GPS_DECIMALS = 4
    }
}
