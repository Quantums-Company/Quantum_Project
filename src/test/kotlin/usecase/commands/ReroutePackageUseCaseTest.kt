package usecase.commands

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.usecase.commands.ReroutePackageUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ReroutePackageUseCaseTest {

    private lateinit var reroutePackage: ReroutePackageUseCase

    @BeforeEach
    fun setUp() {
        reroutePackage = ReroutePackageUseCase()
    }

    @Test
    fun `should change package destination to the new warehouse when rerouted`() {
        // Given
        val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
        val pkg = createPackage()

        // When
        val reroutedPackage = reroutePackage(pkg, newDestination)

        // Then
        assertThat(reroutedPackage.destinationWarehouse).isEqualTo(newDestination)
    }

    @Test
    fun `should keep package identity and cargo details unchanged when rerouted`() {
        // Given
        val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
        val pkg = createPackage()

        // When
        val reroutedPackage = reroutePackage(pkg, newDestination)

        // Then
        assertThat(reroutedPackage.id).isEqualTo(pkg.id)
        assertThat(reroutedPackage.weight).isEqualTo(pkg.weight)
        assertThat(reroutedPackage.priority).isEqualTo(pkg.priority)
        assertThat(reroutedPackage.originWarehouse).isEqualTo(pkg.originWarehouse)
    }

    @Test
    fun `should not modify the original package when rerouted`() {
        // Given
        val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
        val pkg = createPackage()

        // When
        reroutePackage(pkg, newDestination)

        // Then
        assertThat(pkg.destinationWarehouse).isEqualTo(ORIGINAL_DESTINATION)
    }

    @Test
    fun `should throw EntityValidationException when rerouted to its own origin warehouse`() {
        // Given
        val pkg = createPackage()

        // When & Then
        assertThrows<EntityValidationException> {
            reroutePackage(pkg, ORIGIN)
        }
    }

    private fun createPackage() = Package(
        id = "PKG-001",
        weight = 10.0,
        priority = Priority.STANDARD,
        originWarehouse = ORIGIN,
        destinationWarehouse = ORIGINAL_DESTINATION
    )

    private companion object {
        val ORIGIN = createWarehouse(id = "WH-001", name = "Ramallah Hub")
        val ORIGINAL_DESTINATION = createWarehouse(id = "WH-002", name = "Nablus Depot")

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "West Bank",
            longitude = 35.2,
            latitude = 31.9
        )
    }
}