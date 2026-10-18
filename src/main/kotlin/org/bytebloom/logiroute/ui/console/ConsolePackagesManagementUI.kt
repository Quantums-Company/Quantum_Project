package org.bytebloom.logiroute.ui.console

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.presentation.screen.PackagesManagementScreen

class ConsolePackagesManagementUI : PackagesManagementScreen {
    override fun showLoadingIndicator(isLoading: Boolean) {
        if (isLoading) println("Loading packages...")
    }

    override fun showErrorMessage(errorMessage: String) = println("[ERROR] $errorMessage")

    override fun showPackages(packages: List<Package>) {
        printSection("PACKAGE MANAGEMENT")
        if (packages.isEmpty()) println("No packages found.")
        packages.forEachIndexed { index, pkg ->
            println("${index + 1}. ${pkg.id} | ${pkg.weight} kg | ${pkg.priority} | ${pkg.originWarehouse.id} -> ${pkg.destinationWarehouse.id}")
        }
    }

    override fun showPackageDetails(pkg: Package) {
        printSection("PACKAGE DETAILS — ${pkg.id}")
        println("Weight      : ${pkg.weight} kg")
        println("Priority    : ${pkg.priority}")
        println("Origin      : ${pkg.originWarehouse.id} — ${pkg.originWarehouse.name}")
        println("Destination : ${pkg.destinationWarehouse.id} — ${pkg.destinationWarehouse.name}")
    }
}