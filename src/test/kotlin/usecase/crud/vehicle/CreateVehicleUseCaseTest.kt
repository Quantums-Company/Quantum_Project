package usecase.crud.vehicle

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.usecase.crud.vehicle.CreateVehicleUseCase
import org.bytebloom.domain.validator.create.CreateVehicleValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateVehicleUseCaseTest {

    @Test
    fun `should create vehicle successfully`() = runTest {
        // Given
        val vehicleRepository = mockk<VehicleRepository>()
        val validator = mockk<CreateVehicleValidator>()
        val idGenerator = mockk<IdGenerator>()

        val warehouse = Warehouse(
            id = "WH-001",
            name = "Main Warehouse",
            regionalZone = "North",
            longitude = 35.0,
            latitude = 32.0
        )

        every { idGenerator.next(EntityType.VEHICLE) } returns "TRK-001"

        every { validator(any()) } returns ValidatorResult.Valid

        val createdVehicle = Vehicle(
            id = "TRK-001",
            maxCapacityKg = 200.0,
            costPerKm = 5.0,
            currentWarehouse = warehouse
        )

        coEvery { vehicleRepository.create(any()) } returns createdVehicle

        val useCase = CreateVehicleUseCase(
            vehicleRepository,
            validator,
            idGenerator
        )

        // When
        val result = useCase(
            maxCapacityKg = 200.0,
            costPerKm = 5.0,
            currentWarehouse = warehouse
        )

        // Then
        assertEquals(createdVehicle, result)
    }
}
