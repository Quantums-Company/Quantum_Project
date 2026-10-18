package org.bytebloom.logiroute.domain.usecase.classification

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.knn.KNearestNeighborsClassifier
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures
import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord
import org.bytebloom.logiroute.domain.repository.TrainingCargoRepository
import org.bytebloom.logiroute.domain.usecase.classification.ClassifyPackageSectorUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClassifyPackageSectorUseCaseTest {

    private lateinit var trainingCargoRepository: TrainingCargoRepository
    private lateinit var classifyPackageSector: ClassifyPackageSectorUseCase

    @BeforeEach
    fun setUp() {
        trainingCargoRepository = mockk()
        classifyPackageSector = ClassifyPackageSectorUseCase(trainingCargoRepository, KNearestNeighborsClassifier())
    }

    @Test
    fun `should classify package into west when its destination is near west records`() = runTest {
        // Given
        coEvery { trainingCargoRepository.getTrainingRecords() } returns WEST_RECORDS + EAST_RECORDS
        val incomingPackage = createPackage(destinationLatitude = 30.1, destinationLongitude = -100.2, weight = 110.0)

        // When
        val region = classifyPackageSector(incomingPackage)

        // Then
        assertThat(region).isEqualTo(Region.WEST)
    }

    @Test
    fun `should classify package into east when its destination is near east records`() = runTest {
        // Given
        coEvery { trainingCargoRepository.getTrainingRecords() } returns WEST_RECORDS + EAST_RECORDS
        val incomingPackage = createPackage(destinationLatitude = 40.1, destinationLongitude = -75.1, weight = 480.0)

        // When
        val region = classifyPackageSector(incomingPackage)

        // Then
        assertThat(region).isEqualTo(Region.EAST)
    }

    @Test
    fun `should load training records from repository exactly once per classification`() = runTest {
        // Given
        coEvery { trainingCargoRepository.getTrainingRecords() } returns WEST_RECORDS + EAST_RECORDS
        val incomingPackage = createPackage(destinationLatitude = 30.0, destinationLongitude = -100.0, weight = 100.0)

        // When
        classifyPackageSector(incomingPackage)

        // Then
        coVerify(exactly = 1) { trainingCargoRepository.getTrainingRecords() }
    }

    @Test
    fun `should fail when there are no training records`() = runTest {
        // Given
        coEvery { trainingCargoRepository.getTrainingRecords() } returns emptyList()
        val incomingPackage = createPackage(destinationLatitude = 30.0, destinationLongitude = -100.0, weight = 100.0)

        // When
        val result = runCatching { classifyPackageSector(incomingPackage) }

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    private fun createPackage(destinationLatitude: Double, destinationLongitude: Double, weight: Double) = Package(
        id = "PKG-001",
        weight = weight,
        priority = Priority.STANDARD,
        originWarehouse = ORIGIN_WAREHOUSE,
        destinationWarehouse = Warehouse("WH-002", "Destination", "CENTRAL", destinationLongitude, destinationLatitude)
    )

    private companion object {
        val ORIGIN_WAREHOUSE = Warehouse("WH-001", "Origin", "CENTRAL", longitude = -90.0, latitude = 35.0)

        val WEST_RECORDS = listOf(
            westRecord(30.0, -100.0, 100.0),
            westRecord(30.2, -100.4, 120.0),
            westRecord(29.8, -99.9, 90.0),
            westRecord(30.1, -100.1, 105.0),
            westRecord(29.9, -100.3, 115.0)
        )

        val EAST_RECORDS = listOf(
            eastRecord(40.0, -75.0, 500.0),
            eastRecord(40.2, -75.3, 470.0),
            eastRecord(39.9, -74.8, 490.0),
            eastRecord(40.1, -75.2, 510.0),
            eastRecord(40.3, -74.9, 460.0)
        )

        fun westRecord(latitude: Double, longitude: Double, weight: Double) =
            LabeledCargoRecord(CargoFeatures(latitude, longitude, weight), Region.WEST)

        fun eastRecord(latitude: Double, longitude: Double, weight: Double) =
            LabeledCargoRecord(CargoFeatures(latitude, longitude, weight), Region.EAST)
    }
}