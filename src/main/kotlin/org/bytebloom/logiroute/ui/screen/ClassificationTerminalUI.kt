package org.bytebloom.logiroute.ui.screen

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.presentation.presenter.ClassificationPresenter
import org.bytebloom.logiroute.presentation.screen.ClassificationScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.table
import org.bytebloom.logiroute.ui.terminal.writeLines

/** The terminal implementation of [ClassificationScreen]. */
class ClassificationTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (ClassificationScreen) -> ClassificationPresenter
) : TerminalScreenBase(terminal, style, uiScope), ClassificationScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var packages: List<Package> = emptyList()

    override suspend fun show() {
        terminal.writeLine()
        terminal.writeLine(style.bold("Classify a package (KNN)"))

        packages = emptyList()
        loadingMessage = "Loading packages…"
        presenter.onViewReady().join()
        if (packages.isEmpty()) return

        val chosen = terminal.chooseFrom(PACKAGE_PROMPT, packages, Package::id, style)
            ?: return

        loadingMessage = "Running k-nearest-neighbours…"
        presenter.onClassify(chosen.id).join()
    }

    override fun showPackages(packages: List<Package>) {
        this.packages = packages
        if (packages.isEmpty()) {
            terminal.writeLine(style.yellow("  There are no packages yet."))
            return
        }
        terminal.writeLines(
            style.table(
                headers = listOf("#", "ID", "Weight (kg)", "Priority", "To"),
                rows = packages.mapIndexed { index, pkg ->
                    listOf(
                        (index + 1).toString(),
                        pkg.id,
                        formatNumber(pkg.weight, 1),
                        pkg.priority.name,
                        pkg.destinationWarehouse.name
                    )
                },
                rightAligned = setOf(0, 2)
            )
        )
        terminal.writeLine()
    }

    override fun showClassification(pkg: Package, region: Region) {
        terminal.writeLines(
            style.box(
                title = style.green("${style.glyphs.success} Predicted region"),
                lines = listOf(
                    "Package  ${pkg.id}  (${formatNumber(pkg.weight, 1)}" +
                            " kg ${style.glyphs.arrow} ${pkg.destinationWarehouse.name})",
                    "Region   ${coloured(region)}"
                )
            )
        )
    }

    private fun coloured(region: Region): String = when (region) {
        Region.WEST -> style.blue(style.bold(region.name))
        Region.EAST -> style.magenta(style.bold(region.name))
        Region.NORTH -> style.cyan(style.bold(region.name))
        Region.SOUTH -> style.yellow(style.bold(region.name))
        Region.CENTRAL -> style.green(style.bold(region.name))
    }

    private companion object {
        const val PACKAGE_PROMPT = "Package to classify (number or id, Enter to cancel): "
    }
}
