package org.bytebloom.logiroute.domain.validator.update

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.input.update.PackageUpdateInput
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.junit.jupiter.api.Test

class UpdatePackageValidatorTest {

    private val validator = UpdatePackageValidator()

    private fun warehouse(): Warehouse =
        Warehouse(
            id = "WH-001",
            name = "Test Warehouse",
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )

    @Test
    fun `should return same warehouse violation when origin and destination are identical`() {
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
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.SameWarehouse::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ORIGIN_WAREHOUSE)
    }

    @Test
    fun `should return no fields provided violation when update has no fields`() {
        // Given
        val input = PackageUpdateInput(id = "PKG-001")

        // When
        val result = validator(input)

        // Then
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.NoFieldsProvided::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ENTITY)
    }

    @Test
    fun `should return invalid format violation when package id has wrong format`() {
        // Given
        val input = PackageUpdateInput(
            id = "WH-001",
            weight = 10.0
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)

        val formatError = error as ValidatorError.InvalidIdFormat
        assertThat(formatError.field).isEqualTo(ValidatorField.ID)
        assertThat(formatError.entityType).isEqualTo(EntityType.PACKAGE)
    }

    @Test
    fun `should return not positive violation when weight is zero`() {
        // Given
        val input = PackageUpdateInput(
            id = "PKG-001",
            weight = 0.0
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.NotPositive::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.WEIGHT)
    }

    @Test
    fun `should return blank violation when origin warehouse id is blank`() {
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
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ORIGIN_WAREHOUSE)
    }

    @Test
    fun `should return blank violation when destination warehouse id is blank`() {
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
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.DESTINATION_WAREHOUSE)
    }

    @Test
    fun `should return valid when priority is updated`() {
        // Given
        val input = PackageUpdateInput(
            id = "PKG-001",
            priority = Priority.URGENT
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }
}