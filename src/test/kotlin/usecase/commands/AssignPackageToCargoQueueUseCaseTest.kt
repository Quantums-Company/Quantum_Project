package usecase.commands

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.commands.AssignPackageToCargoQueueUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AssignPackageToCargoQueueUseCaseTest {

    private lateinit var assignPackageToCargoQueue: AssignPackageToCargoQueueUseCase
    private lateinit var warehouse: Warehouse

    @BeforeEach
    fun setUp() {
        assignPackageToCargoQueue = AssignPackageToCargoQueueUseCase()
        warehouse = createWarehouse(id = "WH-001", name = "Ramallah Hub")
    }

    @Test
    fun `should return true when package is assigned to cargo queue`() {
        // Given
        val pkg = createPackage(id = "PKG-001")

        // When
        val isAssigned = assignPackageToCargoQueue(warehouse, pkg)

        // Then
        assertThat(isAssigned).isTrue()
    }

    @Test
    fun `should add package to warehouse cargo queue when assigned`() {
        // Given
        val pkg = createPackage(id = "PKG-001")

        // When
        assignPackageToCargoQueue(warehouse, pkg)

        // Then
        assertThat(warehouse.cargoQueue).containsExactly(pkg)
    }

    @Test
    fun `should keep previously queued packages when a new package is assigned`() {
        // Given
        val firstPackage = createPackage(id = "PKG-001")
        val secondPackage = createPackage(id = "PKG-002")
        assignPackageToCargoQueue(warehouse, firstPackage)

        // When
        assignPackageToCargoQueue(warehouse, secondPackage)

        // Then
        assertThat(warehouse.cargoQueue).containsExactly(firstPackage, secondPackage)
    }

    @Test
    fun `should start with an empty cargo queue before any assignment`() {
        // Given & When
        val newWarehouse = createWarehouse(id = "WH-003", name = "Hebron Depot")

        // Then
        assertThat(newWarehouse.cargoQueue).isEmpty()
    }

    private fun createPackage(id: String) = Package(
        id = id,
        weight = 10.0,
        priority = Priority.STANDARD,
        originWarehouse = warehouse,
        destinationWarehouse = DESTINATION
    )

    private companion object {
        val DESTINATION = createWarehouse(id = "WH-002", name = "Nablus Depot")

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "West Bank",
            longitude = 35.2,
            latitude = 31.9
        )
    }
}
