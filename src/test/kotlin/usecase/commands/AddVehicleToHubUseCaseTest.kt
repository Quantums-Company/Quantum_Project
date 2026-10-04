package usecase.commands

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.usecase.commands.AddVehicleToHubUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddVehicleToHubUseCaseTest {

    private lateinit var addVehicleToHub: AddVehicleToHubUseCase
    private lateinit var hub: Warehouse

    @BeforeEach
    fun setUp() {
        addVehicleToHub = AddVehicleToHubUseCase()
        hub = createWarehouse(id = "WH-002", name = "Nablus Hub")
    }

    @Test
    fun `should station vehicle in the hub when added`() {
        // Given
        val vehicle = createVehicle(id = "TRK-001")

        // When
        val stationedVehicle = addVehicleToHub(hub, vehicle)

        // Then
        assertThat(hub.hasVehicle(stationedVehicle)).isTrue()
    }

    @Test
    fun `should update vehicle current warehouse to the hub when added`() {
        // Given
        val vehicle = createVehicle(id = "TRK-001")

        // When
        val stationedVehicle = addVehicleToHub(hub, vehicle)

        // Then
        assertThat(stationedVehicle.currentWarehouse).isEqualTo(hub)
    }

    @Test
    fun `should keep vehicle identity and specifications unchanged when added`() {
        // Given
        val vehicle = createVehicle(id = "TRK-001")

        // When
        val stationedVehicle = addVehicleToHub(hub, vehicle)

        // Then
        assertThat(stationedVehicle.id).isEqualTo(vehicle.id)
        assertThat(stationedVehicle.maxCapacityKg).isEqualTo(vehicle.maxCapacityKg)
        assertThat(stationedVehicle.costPerKm).isEqualTo(vehicle.costPerKm)
    }

    @Test
    fun `should not modify the original vehicle when added`() {
        // Given
        val vehicle = createVehicle(id = "TRK-001")

        // When
        addVehicleToHub(hub, vehicle)

        // Then
        assertThat(vehicle.currentWarehouse).isEqualTo(PREVIOUS_WAREHOUSE)
    }

    @Test
    fun `should keep previously stationed vehicles when a new vehicle is added`() {
        // Given
        val firstVehicle = addVehicleToHub(hub, createVehicle(id = "TRK-001"))

        // When
        val secondVehicle = addVehicleToHub(hub, createVehicle(id = "TRK-002"))

        // Then
        assertThat(hub.stationedVehicles).containsExactly(firstVehicle, secondVehicle)
    }

    private fun createVehicle(id: String) = Vehicle(
        id = id,
        maxCapacityKg = 1000.0,
        costPerKm = 2.5,
        currentWarehouse = PREVIOUS_WAREHOUSE
    )

    private companion object {
        val PREVIOUS_WAREHOUSE = createWarehouse(id = "WH-001", name = "Ramallah Hub")

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "West Bank",
            longitude = 35.2,
            latitude = 31.9
        )
    }
}