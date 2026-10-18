package org.bytebloom.logiroute.ui.console

import org.bytebloom.logiroute.presentation.screen.CommandPatternScreen

class ConsoleCommandPatternUI : CommandPatternScreen {
    override fun showLoadingIndicator(isLoading: Boolean) {
        if (isLoading) println("Preparing reversible command demo...")
    }

    override fun showErrorMessage(errorMessage: String) = println("[ERROR] $errorMessage")

    override fun showTitle() {
        printSection("TASK 5 — COMMAND PATTERN DISPATCH PANEL")
        println("Demonstrates Execute, Undo, Redo and a shared command history.")
    }

    override fun showStep(title: String, succeeded: Boolean, details: String) {
        val result = if (succeeded) "SUCCESS" else "FAILED"
        println("\n[$result] $title")
        println("  $details")
    }

    override fun showState(
        label: String,
        queuedPackageIds: List<String>,
        stationedVehicleIds: List<String>,
        canUndo: Boolean,
        canRedo: Boolean
    ) {
        println("  State — $label")
        println("    Cargo queue : ${queuedPackageIds.ifEmpty { listOf("(empty)") }.joinToString()}")
        println("    Vehicles    : ${stationedVehicleIds.ifEmpty { listOf("(none)") }.joinToString()}")
        println("    Undo: ${if (canUndo) "available" else "empty"} | Redo: ${if (canRedo) "available" else "empty"}")
    }

    override fun showFinished(message: String) {
        println("\n$message")
    }
}