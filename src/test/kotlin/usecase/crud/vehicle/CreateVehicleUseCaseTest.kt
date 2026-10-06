package usecase.crud.vehicle

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.usecase.crud.vehicle.CreateVehicleUseCase
import org.bytebloom.domain.model.exception.EntityValidationException
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateVehicleUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val idGenerator = mockk<IdGenerator>()

    private val defaultWarehouse = Warehouse(
        id = "WH-001",
        name = "Main Warehouse",
        regionalZone = "North",
        longitude = 35.0,
        latitude = 32.0
    )

    private val useCase = CreateVehicleUseCase(
        vehicleRepository = vehicleRepository,
        idGenerator = idGenerator
    )

    @Test
    fun `should create vehicle successfully when data is valid`() = runTest {
        every { idGenerator.next(EntityType.VEHICLE) } returns "TRK-001"

        val createdVehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = 500.0,
            costPerKm = 3.5,
            currentWarehouse = defaultWarehouse
        )

        coEvery { vehicleRepository.create(any()) } returns createdVehicle

        val result = useCase(
            maxCapacityKg = 500.0,
            costPerKm = 3.5,
            currentWarehouse = defaultWarehouse
        )

        assertEquals(createdVehicle, result)
    }

    @Test
    fun `should throw validation exception when user enters zero or negative capacity`() = runTest {
        every { idGenerator.next(EntityType.VEHICLE) } returns "TRK-001"

        assertThrows<EntityValidationException> {
            useCase(
                maxCapacityKg = 0.0,
                costPerKm = 4.0,
                currentWarehouse = defaultWarehouse
            )
        }
    }

    @Test
    fun `should throw validation exception when user enters negative cost per km`() = runTest {
        every { idGenerator.next(EntityType.VEHICLE) } returns "TRK-001"

        assertThrows<EntityValidationException> {
            useCase(
                maxCapacityKg = 300.0,
                costPerKm = -1.2,
                currentWarehouse = defaultWarehouse
            )
        }
    }
}