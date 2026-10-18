package org.bytebloom.logiroute.presentation.ui.console.screen

import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import org.bytebloom.logiroute.presentation.model.FleetEventUi
import org.bytebloom.logiroute.presentation.model.VehicleTelemetryUi
import org.bytebloom.logiroute.presentation.presenter.TelemetryPresenter
import org.bytebloom.logiroute.presentation.presenter.TelemetryState
import org.bytebloom.logiroute.presentation.presenter.TelemetryStatus
import org.bytebloom.logiroute.presentation.ui.console.ConsoleStyle
import org.bytebloom.logiroute.presentation.ui.console.Terminal
import org.bytebloom.logiroute.presentation.ui.console.widget.errorBox
import org.bytebloom.logiroute.presentation.ui.console.widget.formatNumber
import org.bytebloom.logiroute.presentation.ui.console.widget.padVisible
import org.bytebloom.logiroute.presentation.ui.console.widget.percentBar
import org.bytebloom.logiroute.presentation.ui.console.widget.table
import org.bytebloom.logiroute.presentation.ui.console.writeLines

class TelemetryScreen(
    private val presenter: TelemetryPresenter,
    private val terminal: Terminal,
    private val style: ConsoleStyle
) : ConsoleScreen {

    override suspend fun show() {
        presenter.start()
        terminal.writeLine()
        terminal.writeLine(style.bold(style.cyan("Live fleet telemetry")) + style.dim("   press Enter to stop"))
        terminal.writeLine()

        var printedUpTo = 0L

        coroutineScope {
            val logPrinter = launch {
                presenter.state.collect { state -> printedUpTo = printNewEvents(state, printedUpTo) }
            }

            // Two ways to leave: the user presses Enter, or the stream ends/fails on its own.
            val enterPressed = async { terminal.readLine() }
            val streamEnded = async {
                presenter.state.first { it.status == TelemetryStatus.FAILED || it.status == TelemetryStatus.STOPPED }
            }
            select {
                enterPressed.onAwait { }
                streamEnded.onAwait { }
            }
            enterPressed.cancel()
            streamEnded.cancel()

            presenter.stop()
            logPrinter.cancelAndJoin()
        }

        summarize(presenter.state.value, printedUpTo)
    }

    private fun summarize(state: TelemetryState, alreadyPrinted: Long) {
        printNewEvents(state, alreadyPrinted)
        val error = state.error
        terminal.writeLine()
        if (state.status == TelemetryStatus.FAILED && error != null) {
            terminal.writeLines(style.errorBox(error))
            return
        }
        terminal.writeLine(style.green("${style.glyphs.success} Telemetry stopped.") + style.dim("  ${state.eventCount} events received."))
        if (state.vehicles.isNotEmpty()) {
            terminal.writeLines(
                style.table(
                    headers = listOf("Vehicle", "Speed (km/h)", "Fuel", "GPS"),
                    rows = state.vehicles.map {
                        listOf(it.vehicleId, formatNumber(it.speedKph, 0), style.percentBar(it.fuelPercent), gps(it))
                    },
                    rightAligned = setOf(1)
                )
            )
        }
    }

    private fun printNewEvents(state: TelemetryState, printedUpTo: Long): Long {
        var last = printedUpTo
        state.events.filter { it.sequence > printedUpTo }.forEach { event ->
            terminal.writeLine(format(event))
            last = event.sequence
        }
        return last
    }

    private fun format(event: FleetEventUi): String {
        val number = style.dim("#" + event.sequence.toString().padStart(SEQUENCE_WIDTH, '0'))
        val divider = style.dim(style.glyphs.vertical)
        return when (event) {
            is FleetEventUi.Reading -> {
                val t = event.telemetry
                listOf(
                    number,
                    style.bold(padVisible(t.vehicleId, VEHICLE_WIDTH)),
                    padVisible(formatNumber(t.speedKph, 0), SPEED_WIDTH, alignRight = true) + " km/h",
                    "fuel " + style.percentBar(t.fuelPercent),
                    style.dim(gps(t))
                ).joinToString(" $divider ")
            }

            is FleetEventUi.DelayAlert ->
                listOf(
                    number,
                    style.red("${style.glyphs.warning} DELAY  ${event.vehicleId} slowed to ${formatNumber(event.speedKph, 0)} km/h")
                ).joinToString(" $divider ")
        }
    }

    private fun gps(t: VehicleTelemetryUi) = "${formatNumber(t.latitude, GPS_DECIMALS)}, ${formatNumber(t.longitude, GPS_DECIMALS)}"

    private companion object {
        const val SEQUENCE_WIDTH = 4
        const val VEHICLE_WIDTH = 8
        const val SPEED_WIDTH = 3
        const val GPS_DECIMALS = 4
    }
}
