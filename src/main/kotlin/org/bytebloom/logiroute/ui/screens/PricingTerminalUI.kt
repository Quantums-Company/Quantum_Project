package org.bytebloom.logiroute.ui.screens

import kotlinx.coroutines.CoroutineScope
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.pricing.decorator.DecoratorFee
import org.bytebloom.logiroute.domain.pricing.decorator.DecoratorType
import org.bytebloom.logiroute.domain.pricing.strategy.ShippingStrategyType
import org.bytebloom.logiroute.presentation.presenter.PricingPresenter
import org.bytebloom.logiroute.presentation.screens.PricingScreen
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle
import org.bytebloom.logiroute.ui.terminal.Terminal
import org.bytebloom.logiroute.ui.terminal.TerminalScreenBase
import org.bytebloom.logiroute.ui.terminal.askDouble
import org.bytebloom.logiroute.ui.terminal.chooseFrom
import org.bytebloom.logiroute.ui.terminal.confirm
import org.bytebloom.logiroute.ui.terminal.widget.box
import org.bytebloom.logiroute.ui.terminal.widget.formatNumber
import org.bytebloom.logiroute.ui.terminal.widget.packageTable

/** The terminal implementation of [PricingScreen]. */
class PricingTerminalUI(
    terminal: Terminal,
    style: ConsoleStyle,
    uiScope: CoroutineScope,
    presenterFactory: (PricingScreen) -> PricingPresenter
) : TerminalScreenBase(terminal, style, uiScope), PricingScreen {

    private val presenter by lazy { presenterFactory(this) }

    @Volatile
    private var packages: List<Package> = emptyList()

    override suspend fun show() {
        heading("Pricing")
        loadingMessage = "Loading packages…"
        presenter.onViewReady().join()
        if (packages.isEmpty()) {
            printWarning("There are no packages to price.")
            return
        }
        printLines(style.packageTable(packages, numbered = true))

        val pkg = terminal.chooseFrom("Package (index or id, Enter to go back): ",
            packages, Package::id, style) ?: return
        val strategy = terminal.chooseFrom(
            "Strategy ${ShippingStrategyType.entries
                .mapIndexed { i, s -> "${i + 1}=${s.name}" }.joinToString()} : ",
            ShippingStrategyType.entries,
            ShippingStrategyType::name,
            style
        ) ?: return

        val decorators = mutableListOf<DecoratorFee>()
        while (terminal.confirm("Add an extra fee (cold chain, insurance, fragile handling)?")) {
            val type = terminal.chooseFrom(
                "Fee ${DecoratorType.entries.mapIndexed { i, t -> "${i + 1}=${t.name}" }.joinToString()} : ",
                DecoratorType.entries,
                DecoratorType::name,
                style
            ) ?: break
            val value = terminal.askDouble(valuePrompt(type), style) ?: break
            decorators += DecoratorFee(type, value)
        }

        loadingMessage = "Calculating the price…"
        presenter.onCalculate(pkg.id, strategy, decorators).join()
    }

    private fun valuePrompt(type: DecoratorType): String = when (type) {
        DecoratorType.COLD_CHAIN -> "Cold-chain multiplier (for example 1.2): "
        DecoratorType.EXPRESS_INSURANCE -> "Insurance premium (amount): "
        DecoratorType.FRAGILE_HANDLING -> "Handling fee (amount): "
    }

    override fun showPackages(packages: List<Package>) {
        this.packages = packages
    }

    override fun showPrice(
        pkg: Package,
        strategy: ShippingStrategyType,
        decorators: List<DecoratorFee>,
        price: Double
    ) =
        printLines(
            style.box(
                title = style.green("${style.glyphs.success} Price for ${pkg.id}"),
                lines = listOf(
                    "Route      ${pkg.originWarehouse.name} → ${pkg.destinationWarehouse.name}"
                        .replace("→", style.glyphs.arrow),
                    "Weight     ${formatNumber(pkg.weight, 1)} kg",
                    "Strategy   ${strategy.name}",
                    "Extras     ${decorators.joinToString { "${it.type.name} (${formatNumber(it.value)})" }
                        .ifEmpty { "none" }}",
                    "Total      ${formatNumber(price)}"
                )
            )
        )
}

