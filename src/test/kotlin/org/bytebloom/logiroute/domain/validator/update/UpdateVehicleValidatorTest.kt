package org.bytebloom.logiroute.domain.validator.update

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.input.update.VehicleUpdateInput
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.update.UpdateVehicleValidator
import org.junit.jupiter.api.Test

class UpdateVehicleValidatorTest {

    private val validator = UpdateVehicleValidator()

    @Test
    fun `should return valid when update data is correct`() {
        // Given
        val input = VehicleUpdateInput(
            id = "TRK-001",
            maxCapacityKg = 300.0,
            costPerKm = null,
            currentWarehouse = null
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return no fields provided violation when no update fields are provided`() {
        // Given
        val input = VehicleUpdateInput(
            id = "TRK-001",
            maxCapacityKg = null,
            costPerKm = null,
            currentWarehouse = null
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)

        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(
            ValidatorError.NoFieldsProvided::class.java
        )
        assertThat(error.field).isEqualTo(ValidatorField.ENTITY)
    }
}