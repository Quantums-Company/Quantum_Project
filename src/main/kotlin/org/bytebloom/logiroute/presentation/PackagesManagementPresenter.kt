package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.presentation.screen.PackagesManagementScreen

class PackagesManagementPresenter(
    private val getAllPackages: GetAllPackagesUseCase,
    private val screen: PackagesManagementScreen
) {
    private var loadedPackages = emptyList<Package>()

    suspend fun loadPackages() {
        screen.showLoadingIndicator(true)
        try {
            loadedPackages = getAllPackages()
            screen.showPackages(loadedPackages)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            screen.showErrorMessage(error.userMessage())
        } finally {
            screen.showLoadingIndicator(false)
        }
    }

    fun showPackage(packageId: String) {
        val pkg = loadedPackages.firstOrNull { it.id.equals(packageId.trim(), ignoreCase = true) }
        if (pkg == null) screen.showErrorMessage("Package '$packageId' was not found.")
        else screen.showPackageDetails(pkg)
    }
}
