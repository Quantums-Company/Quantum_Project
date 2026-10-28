package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Region

/** What the "classify a package (KNN)" screen must be able to show. Implemented by the UI. */
interface ClassificationScreen : BaseScreen {

    fun showPackages(packages: List<Package>)

    fun showClassification(pkg: Package, region: Region)
}
