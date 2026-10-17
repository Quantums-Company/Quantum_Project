package data.repository.training

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.training.PackageHistoryTrainingCargoRepository
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PackageHistoryTrainingCargoRepositoryTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var trainingCargoRepository: PackageHistoryTrainingCargoRepository

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        trainingCargoRepository = PackageHistoryTrainingCargoRepository(packageRepository)
    }

    @Test
    fun `should build features from destination coordinates and package weight`() = runTest {
        // Given
        val destination = Warehouse("WH-002", "West Hub", "WEST", longitude = -100.0, latitude = 30.0)
        coEvery { packageRepository.getAll() } returns listOf(createPackage("PKG-001", 120.0, destination))

        // When
        val records = trainingCargoRepository.getTrainingRecords()

        // Then
        assertThat(records.single().features).isEqualTo(CargoFeatures(latitude = 30.0, longitude = -100.0, weightKg = 120.0))
    }

    @Test
    fun `should label record with destination regional zone`() = runTest {
        // Given
        val destination = Warehouse("WH-002", "East Hub", "EAST", longitude = -75.0, latitude = 40.0)
        coEvery { packageRepository.getAll() } returns listOf(createPackage("PKG-001", 80.0, destination))

        // When
        val records = trainingCargoRepository.getTrainingRecords()

        // Then
        assertThat(records.single().sector).isEqualTo(Region.EAST)
    }

    @Test
    fun `should match regional zone ignoring case and surrounding spaces`() = runTest {
        // Given
        val destination = Warehouse("WH-002", "North Hub", " north ", longitude = -90.0, latitude = 45.0)
        coEvery { packageRepository.getAll() } returns listOf(createPackage("PKG-001", 50.0, destination))

        // When
        val records = trainingCargoRepository.getTrainingRecords()

        // Then
        assertThat(records.single().sector).isEqualTo(Region.NORTH)
    }

    @Test
    fun `should skip packages whose regional zone is unknown`() = runTest {
        // Given
        val knownDestination = Warehouse("WH-002", "South Hub", "SOUTH", longitude = -95.0, latitude = 28.0)
        val unknownDestination = Warehouse("WH-003", "Mystery Hub", "MARS", longitude = -95.0, latitude = 28.0)
        coEvery { packageRepository.getAll() } returns listOf(
            createPackage("PKG-001", 60.0, knownDestination),
            createPackage("PKG-002", 70.0, unknownDestination)
        )

        // When
        val records = trainingCargoRepository.getTrainingRecords()

        // Then
        assertThat(records.map { it.sector }).containsExactly(Region.SOUTH)
    }

    private fun createPackage(id: String, weight: Double, destination: Warehouse) = Package(
        id = id,
        weight = weight,
        priority = Priority.STANDARD,
        originWarehouse = ORIGIN_WAREHOUSE,
        destinationWarehouse = destination
    )

    private companion object {
        val ORIGIN_WAREHOUSE = Warehouse("WH-001", "Origin", "CENTRAL", longitude = -90.0, latitude = 35.0)
    }
}