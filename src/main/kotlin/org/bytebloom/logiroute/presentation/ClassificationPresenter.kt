package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.presentation.screen.ClassificationScreen

class ClassificationPresenter(
    private val getAllPackages: GetAllPackagesUseCase,
    private val classifyPackage: ClassifyPackageSectorUseCase,
    private val screen: ClassificationScreen
) {
    private var packages = emptyList<Package>()

    suspend fun loadPackages() {
        screen.showLoadingIndicator(true)
        try {
            packages = getAllPackages()
            screen.showPackages(packages)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            screen.showErrorMessage(error.userMessage())
        } finally {
            screen.showLoadingIndicator(false)
        }
    }

    suspend fun classify(packageId: String) {
        screen.showLoadingIndicator(true)
        try {
            if (packages.isEmpty()) packages = getAllPackages()
            val pkg = packages.firstOrNull { it.id.equals(packageId.trim(), ignoreCase = true) }
                ?: return screen.showErrorMessage("Package '$packageId' was not found.")
            screen.showClassification(pkg.id, classifyPackage(pkg))
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            screen.showErrorMessage(error.userMessage())
        } finally {
            screen.showLoadingIndicator(false)
        }
    }
}
