package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.knn.KNearestNeighborsClassifier
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.presentation.FakePackageRepository
import org.bytebloom.logiroute.presentation.FakeTrainingCargoRepository
import org.bytebloom.logiroute.presentation.model.RegionUi
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.parcel
import org.bytebloom.logiroute.presentation.trainingRecord
import org.bytebloom.logiroute.presentation.warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ClassificationPresenterTest {

    private val origin = warehouse("WH-001", name = "Ramallah")
    private val eastDestination = warehouse("WH-002", name = "Jericho", latitude = 31.86, longitude = 35.46)

    // Five historic parcels around Jericho were delivered in the EAST region, five around Hebron in the SOUTH.
    private val training = List(5) { i -> trainingRecord(Region.EAST, 31.86 + i * 0.001, 35.46, 10.0) } +
            List(5) { i -> trainingRecord(Region.SOUTH, 31.53 + i * 0.001, 35.09, 10.0) }

    private fun presenter(
        scope: CoroutineScope,
        packages: FakePackageRepository = FakePackageRepository(listOf(parcel("PKG-001", origin, eastDestination))),
        records: List<LabeledCargoRecord> = training
    ) = ClassificationPresenter(
        getAllPackages = GetAllPackagesUseCase(packages),
        classifyPackage = ClassifyPackageSectorUseCase(FakeTrainingCargoRepository(records), KNearestNeighborsClassifier()),
        parentScope = scope,
        computeDispatcher = Dispatchers.Unconfined
    )

    private suspend fun ClassificationPresenter.loaded(): ClassificationPresenter {
        load()
        state.first { it.status == ClassificationStatus.READY }
        return this
    }

    private suspend fun ClassificationPresenter.result(packageId: String): ClassificationState {
        classify(packageId)
        return state.first { it.status == ClassificationStatus.CLASSIFIED || it.status == ClassificationStatus.FAILED }
    }

    @Test
    fun `lists the packages once loaded`() = runTest {
        // Given
        val presenter = presenter(backgroundScope)

        // When
        presenter.load()
        val state = presenter.state.first { it.status == ClassificationStatus.READY }

        // Then
        assertEquals(listOf("PKG-001"), state.packages.map { it.id })
    }

    @Test
    fun `shows classifying right after a classification is requested`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        presenter.classify("PKG-001")

        // Then
        assertEquals(ClassificationStatus.CLASSIFYING, presenter.state.value.status)
    }

    @Test
    fun `predicts the region of the nearest historic parcels`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val state = presenter.result("PKG-001")

        // Then
        assertEquals(RegionUi.EAST, state.result?.region)
    }

    @Test
    fun `remembers which package was classified`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val state = presenter.result("pkg-001")

        // Then
        assertEquals("PKG-001", state.result?.packageId)
    }

    @Test
    fun `rejects an unknown package`() = runTest {
        // Given
        val presenter = presenter(backgroundScope).loaded()

        // When
        val state = presenter.result("PKG-404")

        // Then
        assertEquals(UiError.InvalidInput("Package 'PKG-404' was not found."), state.error)
    }

    @Test
    fun `explains why classification is impossible without training data`() = runTest {
        // Given
        val presenter = presenter(backgroundScope, records = emptyList()).loaded()

        // When
        val state = presenter.result("PKG-001")

        // Then
        assertEquals(UiError.Unexpected("Training records must not be empty"), state.error)
    }

    @Test
    fun `fails with a network error when the packages cannot be loaded`() = runTest {
        // Given
        val presenter = presenter(
            backgroundScope,
            packages = FakePackageRepository(failure = NetworkUnavailableException("offline"))
        )

        // When
        presenter.load()
        val state = presenter.state.first { it.status == ClassificationStatus.FAILED }

        // Then
        assertEquals(UiError.Network("offline"), state.error)
    }
}
