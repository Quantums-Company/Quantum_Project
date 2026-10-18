package org.bytebloom.logiroute.presentation.screen

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Region

interface ClassificationScreen {
    fun showLoadingIndicator(isLoading: Boolean)
    fun showErrorMessage(errorMessage: String)
    fun showPackages(packages: List<Package>)
    fun showClassification(packageId: String, region: Region)
}
