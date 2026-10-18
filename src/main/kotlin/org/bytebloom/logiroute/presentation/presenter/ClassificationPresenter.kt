package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.presentation.model.ClassificationUi
import org.bytebloom.logiroute.presentation.model.PackageUi
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.presenter.mapper.toUi

enum class ClassificationStatus {
    IDLE,
    LOADING,
    READY,
    CLASSIFYING,
    CLASSIFIED,
    FAILED
}

data class ClassificationState(
    val status: ClassificationStatus = ClassificationStatus.IDLE,
    val packages: List<PackageUi> = emptyList(),
    val result: ClassificationUi? = null,
    val error: UiError? = null
)

class ClassificationPresenter(
    private val getAllPackages: GetAllPackagesUseCase,
    private val classifyPackage: ClassifyPackageSectorUseCase,
    parentScope: CoroutineScope,
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) : Presenter<ClassificationState>(ClassificationState(), parentScope) {

    private val packagesById = MutableStateFlow<Map<String, Package>>(emptyMap())
    private var job: Job? = null

    fun load() {
        job?.cancel()
        update { it.copy(status = ClassificationStatus.LOADING, result = null, error = null) }

        job = scope.launch {
            val packages = guarded(onFailure = ::showFailure) { getAllPackages() } ?: return@launch
            packagesById.value = packages.associateBy { it.id }
            update {
                ClassificationState(
                    status = ClassificationStatus.READY,
                    packages = packages.map { pkg -> pkg.toUi() }
                )
            }
        }
    }

    fun classify(packageId: String) {
        job?.cancel()
        update { it.copy(status = ClassificationStatus.CLASSIFYING, result = null, error = null) }

        job = scope.launch {
            guarded(onFailure = ::showFailure) { classifyOne(packageId) }
        }
    }

    private suspend fun classifyOne(packageId: String) {
        val target = packagesById.value.values.firstOrNull { it.id.equals(packageId.trim(), ignoreCase = true) }
            ?: return showFailure(UiError.InvalidInput("Package '$packageId' was not found."))

        // KNN scans the whole training set: CPU-bound work stays off the UI dispatcher.
        val region = withContext(computeDispatcher) { classifyPackage(target) }

        update {
            it.copy(
                status = ClassificationStatus.CLASSIFIED,
                result = ClassificationUi(packageId = target.id, region = region.toUi())
            )
        }
    }

    private fun showFailure(error: UiError) =
        update { it.copy(status = ClassificationStatus.FAILED, result = null, error = error) }
}
