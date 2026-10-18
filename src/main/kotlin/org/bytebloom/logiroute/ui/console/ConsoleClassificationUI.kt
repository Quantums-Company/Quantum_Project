package org.bytebloom.logiroute.ui.console

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.presentation.screen.ClassificationScreen

class ConsoleClassificationUI : ClassificationScreen {
    override fun showLoadingIndicator(isLoading: Boolean) {
        if (isLoading) println("Classifying package...")
    }

    override fun showErrorMessage(errorMessage: String) = println("[ERROR] $errorMessage")

    override fun showPackages(packages: List<Package>) {
        printSection("PACKAGE CLASSIFICATION — SELECT A PACKAGE ID")
        packages.forEach { println("${it.id} | ${it.weight} kg | ${it.priority}") }
    }

    override fun showClassification(packageId: String, region: Region) {
        printSection("CLASSIFICATION RESULT")
        println("Package: $packageId")
        println("Predicted sector: $region")
    }
}