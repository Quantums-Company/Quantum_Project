package usecase.queries.backhaul

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.NetworkUnavailableException
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.usecase.queries.backhaul.FindBackhaulOpportunityUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class FindBackhaulOpportunityUseCaseTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var findBackhaulOpportunity: FindBackhaulOpportunityUseCase
    private lateinit var vehicle: Vehicle

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        findBackhaulOpportunity = FindBackhaulOpportunityUseCase(packageRepository)
        vehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = VEHICLE_CAPACITY_KG,
            costPerKm = 2.5,
            currentWarehouse = CURRENT_LOCATION
        )
    }

    @Test
    fun `should select only packages travelling from destination back to vehicle location`() = runTest {
        // Given
        val returnPackages = listOf(
            createPackage("PKG-001", 30.0, from = DESTINATION, to = CURRENT_LOCATION),
            createPackage("PKG-002", 40.0, from = DESTINATION, to = CURRENT_LOCATION)
        )
        val unrelatedPackages = listOf(
            createPackage("PKG-003", 20.0, from = CURRENT_LOCATION, to = DESTINATION),
            createPackage("PKG-004", 10.0, from = DESTINATION, to = OTHER_WAREHOUSE)
        )
        coEvery { packageRepository.getAll() } returns returnPackages + unrelatedPackages

        // When
        val opportunity = findBackhaulOpportunity(vehicle, DESTINATION)

        // Then
        assertThat(opportunity?.packages).containsExactlyElementsIn(returnPackages)
    }

    @Test
    fun `should describe vehicle route and cargo totals in the opportunity`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(
            createPackage("PKG-001", 30.0, from = DESTINATION, to = CURRENT_LOCATION),
            createPackage("PKG-002", 40.0, from = DESTINATION, to = CURRENT_LOCATION)
        )

        // When
        val opportunity = findBackhaulOpportunity(vehicle, DESTINATION)!!

        // Then
        assertThat(opportunity.vehicleId).isEqualTo(vehicle.id)
        assertThat(opportunity.outboundWarehouseId).isEqualTo(CURRENT_LOCATION.id)
        assertThat(opportunity.returnWarehouseId).isEqualTo(DESTINATION.id)
        assertThat(opportunity.totalCargoWeightKg).isEqualTo(70.0)
        assertThat(opportunity.remainingCapacityKg).isEqualTo(30.0)
    }

    @Test
    fun `should skip package that exceeds remaining capacity and continue with lighter ones`() = runTest {
        // Given
        val firstPackage = createPackage("PKG-001", 80.0, from = DESTINATION, to = CURRENT_LOCATION)
        val tooHeavyPackage = createPackage("PKG-002", 50.0, from = DESTINATION, to = CURRENT_LOCATION)
        val lightPackage = createPackage("PKG-003", 20.0, from = DESTINATION, to = CURRENT_LOCATION)
        coEvery { packageRepository.getAll() } returns listOf(firstPackage, tooHeavyPackage, lightPackage)

        // When
        val opportunity = findBackhaulOpportunity(vehicle, DESTINATION)!!

        // Then
        assertThat(opportunity.packages).containsExactly(firstPackage, lightPackage).inOrder()
        assertThat(opportunity.remainingCapacityKg).isEqualTo(0.0)
    }

    @Test
    fun `should return null when no package travels back to vehicle location`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(
            createPackage("PKG-001", 30.0, from = CURRENT_LOCATION, to = DESTINATION)
        )

        // When
        val opportunity = findBackhaulOpportunity(vehicle, DESTINATION)

        // Then
        assertThat(opportunity).isNull()
    }

    @Test
    fun `should return null when every return package exceeds vehicle capacity`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(
            createPackage("PKG-001", 150.0, from = DESTINATION, to = CURRENT_LOCATION)
        )

        // When
        val opportunity = findBackhaulOpportunity(vehicle, DESTINATION)

        // Then
        assertThat(opportunity).isNull()
    }

    @Test
    fun `should return null when repository has no packages`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns emptyList()

        // When
        val opportunity = findBackhaulOpportunity(vehicle, DESTINATION)

        // Then
        assertThat(opportunity).isNull()
    }

    @Test
    fun `should load packages from repository exactly once`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns emptyList()

        // When
        findBackhaulOpportunity(vehicle, DESTINATION)

        // Then
        coVerify(exactly = 1) { packageRepository.getAll() }
    }

    @Test
    fun `should propagate network exception when repository is unreachable`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } throws NetworkUnavailableException()

        // When & Then
        assertFailsWith<NetworkUnavailableException> { findBackhaulOpportunity(vehicle, DESTINATION) }
    }

    private fun createPackage(id: String, weight: Double, from: Warehouse, to: Warehouse) = Package(
        id = id,
        weight = weight,
        priority = Priority.STANDARD,
        originWarehouse = from,
        destinationWarehouse = to
    )

    private companion object {
        const val VEHICLE_CAPACITY_KG = 100.0
        val CURRENT_LOCATION = createWarehouse(id = "WH-001", name = "Ramallah Hub")
        val DESTINATION = createWarehouse(id = "WH-002", name = "Nablus Depot")
        val OTHER_WAREHOUSE = createWarehouse(id = "WH-003", name = "Hebron Depot")

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "West Bank",
            longitude = 35.2,
            latitude = 31.9
        )
    }
}
