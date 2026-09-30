package usecase.commands

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.commands.ReroutePackageUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReroutePackageUseCaseTest {

    private lateinit var reroutePackage: ReroutePackageUseCase

    @BeforeEach
    fun setUp() {
        reroutePackage = ReroutePackageUseCase()
    }

    @Test
    fun `should change package destination to the new warehouse when rerouted`() {
        // Given
        val originalDestination = createWarehouse(id = "WH-002", name = "Nablus Depot")
        val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
        val pkg = createPackage(destination = originalDestination)

        // When
        val reroutedPackage = reroutePackage(pkg, newDestination)

        // Then
        assertThat(reroutedPackage.destinationWarehouse).isEqualTo(newDestination)
    }

    private fun createWarehouse(id: String, name: String) = Warehouse(
        id = id,
        name = name,
        regionalZone = "West Bank",
        longitude = 35.2,
        latitude = 31.9
    )

    private fun createPackage(destination: Warehouse) = Package(
        id = "PKG-001",
        weight = 10.0,
        priority = Priority.STANDARD,
        originWarehouse = createWarehouse(id = "WH-001", name = "Ramallah Hub"),
        destinationWarehouse = destination
    )
}