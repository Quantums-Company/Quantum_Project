package validator.create

import io.mockk.every
import io.mockk.mockk
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.create.CreatePackageValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class CreatePackageValidatorTest {

    private val validator = CreatePackageValidator()

    @Test
    fun `accepts a valid package`() {
        // Given
        val origin = warehouse("WH-001")
        val destination = warehouse("WH-002")
        val packageItem = Package(
            id = "PKG-001",
            weight = 10.0,
            priority = org.bytebloom.domain.model.Priority.STANDARD,
            originWarehouse = origin,
            destinationWarehouse = destination
        )

        // When
        val result = validator(packageItem)

        // Then
        assertSame(ValidatorResult.Valid, result)
    }

    @Test
    fun `rejects an invalid package id`() {
        // Given
        val packageItem = packageMock(id = "WH-001")

        // When
        val result = validator(packageItem)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.InvalidIdFormat>(
            invalid.violations.single()
        )
        assertEquals(ValidatorField.ID, error.field)
    }

    @Test
    fun `rejects a non positive package weight`() {
        // Given
        val packageItem = packageMock(weight = 0.0)

        // When
        val result = validator(packageItem)

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
        val packageItem = packageMock(originId = "")

        // When
        val result = validator(packageItem)

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
        val packageItem = packageMock(destinationId = "")

        // When
        val result = validator(packageItem)

        // Then
        val invalid = assertIs<ValidatorResult.Invalid>(result)
        val error = assertIs<ValidatorError.Blank>(
            invalid.violations.single()
        )
        assertEquals(ValidatorField.DESTINATION_WAREHOUSE, error.field)
    }

    private fun warehouse(id: String): Warehouse =
        Warehouse(
            id = id,
            name = "Test Warehouse",
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )

    private fun packageMock(
        id: String = "PKG-001",
        weight: Double = 10.0,
        originId: String = "WH-001",
        destinationId: String = "WH-002"
    ): Package {
        val packageItem = mockk<Package>()
        val origin = mockk<Warehouse>()
        val destination = mockk<Warehouse>()

        every { packageItem.id } returns id
        every { packageItem.weight } returns weight
        every { packageItem.originWarehouse } returns origin
        every { packageItem.destinationWarehouse } returns destination
        every { origin.id } returns originId
        every { destination.id } returns destinationId

        return packageItem
    }
}