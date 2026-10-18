package org.bytebloom.logiroute.presentation.screen

import org.bytebloom.logiroute.domain.model.Package

interface PackagesManagementScreen {
    fun showLoadingIndicator(isLoading: Boolean)
    fun showErrorMessage(errorMessage: String)
    fun showPackages(packages: List<Package>)
    fun showPackageDetails(pkg: Package)
}
