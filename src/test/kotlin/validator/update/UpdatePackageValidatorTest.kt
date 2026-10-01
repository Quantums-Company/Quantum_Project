package validator.update

import io.mockk.every
import io.mockk.mockk
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.updateInput.PackageUpdateInput
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.update.UpdatePackageValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class UpdatePackageValidatorTest {

    private val validator = UpdatePackageValidator()

    @Test
    fun `rejects identical origin and destination warehouses`() {
        // Given
        val warehouse = warehouse()
        val input = PackageUpdateInput(
            id = "PKG-001",
            originWarehouse = warehouse,
            destinationWarehouse = warehouse
        )

        // When
        val result = validator(input)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.SameWarehouse>(
            invalid.violations.single()
        )
        assertEquals(ValidatorField.ORIGIN_WAREHOUSE, error.field)
    }

    @Test
    fun `rejects an update with no fields`() {
        // Given
        val input = PackageUpdateInput(id = "PKG-001")

        // When
        val result = validator(input)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.NoFieldsProvided>(
            invalid.violations.single()
        )
        assertEquals(ValidatorField.ENTITY, error.field)
    }

    @Test
    fun `rejects an id with the wrong package format`() {
        // Given
        val input = PackageUpdateInput(
            id = "WH-001",
            weight = 10.0
        )

        // When
        val result = validator(input)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.InvalidIdFormat>(
            invalid.violations.single()
        )
        assertEquals(ValidatorField.ID, error.field)
        assertEquals(EntityType.PACKAGE, error.entityType)
    }

    @Test
    fun `rejects a non positive weight`() {
        // Given
        val input = PackageUpdateInput(
            id = "PKG-001",
            weight = 0.0
        )

        // When
        val result = validator(input)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.NotPositive>(
            invalid.violations.single()
        )
        assertEquals(ValidatorField.WEIGHT, error.field)
    }

    @Test
    fun `rejects a blank origin warehouse id`() {
        // Given
        val origin = mockk<Warehouse>()
        every { origin.id } returns ""

        val input = PackageUpdateInput(
            id = "PKG-001",
            originWarehouse = origin
        )

        // When
        val result = validator(input)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.Blank>(
            invalid.violations.single()
        )
        assertEquals(ValidatorField.ORIGIN_WAREHOUSE, error.field)
    }

    @Test
    fun `rejects a blank destination warehouse id`() {
        // Given
        val destination = mockk<Warehouse>()
        every { destination.id } returns ""

        val input = PackageUpdateInput(
            id = "PKG-001",
            destinationWarehouse = destination
        )

        // When
        val result = validator(input)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.Blank>(
            invalid.violations.single()
        )
        assertEquals(ValidatorField.DESTINATION_WAREHOUSE, error.field)
    }

    @Test
    fun `accepts a valid priority update`() {
        // Given
        val input = PackageUpdateInput(
            id = "PKG-001",
            priority = Priority.URGENT
        )

        // When
        val result = validator(input)

        // Then
        assertSame(ValidatorResult.Valid, result)
    }

    private fun warehouse(): Warehouse =
        Warehouse(
            id = "WH-001",
            name = "Test Warehouse",
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )
}