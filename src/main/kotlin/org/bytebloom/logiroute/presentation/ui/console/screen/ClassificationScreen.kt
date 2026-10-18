package org.bytebloom.logiroute.presentation.ui.console.screen

import org.bytebloom.logiroute.presentation.model.ClassificationUi
import org.bytebloom.logiroute.presentation.model.PackageUi
import org.bytebloom.logiroute.presentation.model.PriorityUi
import org.bytebloom.logiroute.presentation.model.RegionUi
import org.bytebloom.logiroute.presentation.presenter.ClassificationPresenter
import org.bytebloom.logiroute.presentation.presenter.ClassificationStatus
import org.bytebloom.logiroute.presentation.ui.console.ConsoleStyle
import org.bytebloom.logiroute.presentation.ui.console.Terminal
import org.bytebloom.logiroute.presentation.ui.console.widget.box
import org.bytebloom.logiroute.presentation.ui.console.widget.errorBox
import org.bytebloom.logiroute.presentation.ui.console.widget.formatNumber
import org.bytebloom.logiroute.presentation.ui.console.widget.table
import org.bytebloom.logiroute.presentation.ui.console.writeLines

class ClassificationScreen(
    private val presenter: ClassificationPresenter,
    private val terminal: Terminal,
    private val style: ConsoleStyle
) : ConsoleScreen {

    override suspend fun show() {
        presenter.load()
        val loaded = terminal.awaitWithSpinner(style, "Loading packages…", presenter.state) {
            it.status == ClassificationStatus.READY || it.status == ClassificationStatus.FAILED
        }

        val loadError = loaded.error
        if (loaded.status == ClassificationStatus.FAILED && loadError != null) {
            terminal.writeLines(style.errorBox(loadError))
            return
        }
        if (loaded.packages.isEmpty()) {
            terminal.writeLine(style.yellow("There are no packages to classify."))
            return
        }

        showPackages(loaded.packages)
        val chosen = terminal.chooseFrom("Package to classify (number or id, Enter to cancel): ", loaded.packages, { it.id }, style)
            ?: return

        presenter.classify(chosen.id)
        val result = terminal.awaitWithSpinner(style, "Running k-nearest-neighbours…", presenter.state) {
            it.status == ClassificationStatus.CLASSIFIED || it.status == ClassificationStatus.FAILED
        }

        val classification = result.result
        val error = result.error
        when {
            result.status == ClassificationStatus.CLASSIFIED && classification != null -> render(classification, chosen)
            error != null -> terminal.writeLines(style.errorBox(error))
        }
    }

    private fun showPackages(packages: List<PackageUi>) {
        terminal.writeLine()
        terminal.writeLines(
            style.table(
                headers = listOf("#", "ID", "Weight (kg)", "Priority", "To"),
                rows = packages.mapIndexed { index, it ->
                    listOf((index + 1).toString(), it.id, formatNumber(it.weightKg, 1), priorityLabel(it.priority), it.destinationName)
                },
                rightAligned = setOf(0, 2)
            )
        )
        terminal.writeLine()
    }

    private fun render(result: ClassificationUi, chosen: PackageUi) {
        terminal.writeLine()
        terminal.writeLines(
            style.box(
                title = style.green("${style.glyphs.success} Predicted region"),
                lines = listOf(
                    "Package   ${style.bold(result.packageId)}  (${formatNumber(chosen.weightKg, 1)} kg → ${chosen.destinationName})",
                    "Region    ${regionLabel(result.region)}",
                    style.dim("k = 5 nearest historical packages voted for this region")
                )
            )
        )
    }

    private fun regionLabel(region: RegionUi): String = when (region) {
        RegionUi.WEST -> style.blue(style.bold("WEST"))
        RegionUi.EAST -> style.magenta(style.bold("EAST"))
        RegionUi.NORTH -> style.cyan(style.bold("NORTH"))
        RegionUi.SOUTH -> style.yellow(style.bold("SOUTH"))
        RegionUi.CENTRAL -> style.green(style.bold("CENTRAL"))
    }

    private fun priorityLabel(priority: PriorityUi): String = when (priority) {
        PriorityUi.URGENT -> style.red("URGENT")
        PriorityUi.STANDARD -> style.yellow("STANDARD")
        PriorityUi.LOW -> style.green("LOW")
    }
}
