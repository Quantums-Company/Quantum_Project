package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.presentation.screen.ClassificationScreen

class ClassificationPresenter(
    private val getAllPackages: GetAllPackagesUseCase,
    private val classifyPackage: ClassifyPackageSectorUseCase,
    view: ClassificationScreen,
    parentScope: CoroutineScope,
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BasePresenter<ClassificationScreen>(view, parentScope) {

    private class Classified(val pkg: Package, val region: Region)

    @Volatile
    private var packages: List<Package> = emptyList()

    fun onViewReady(): Job = perform(task = { getAllPackages() }) { loaded ->
        packages = loaded
        view.showPackages(loaded)
    }

    fun onClassify(packageId: String): Job =
        perform(task = { classify(packageId) }) { view.showClassification(it.pkg, it.region) }

    private suspend fun classify(packageId: String): Classified {
        val target = packages.firstOrNull { it.id.equals(packageId.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Package '$packageId' was not found.")

        // KNN compares the package with every historical record: CPU-bound, so off the UI dispatcher.
        val region = withContext(computeDispatcher) { classifyPackage(target) }
        return Classified(target, region)
    }
}
