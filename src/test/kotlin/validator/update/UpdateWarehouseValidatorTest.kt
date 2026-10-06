package validator.update

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.input.update.WarehouseUpdateInput
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.update.UpdateWarehouseValidator

class UpdateWarehouseValidatorTest {

    private val validator = UpdateWarehouseValidator()

    private fun violationsOf(result: ValidatorResult): List<ValidatorError> {
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        return (result as ValidatorResult.Invalid).violations
    }

    @Test
    fun `returns Valid when id is valid and one field is provided`() {
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name")

        val result = validator(input)

        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `returns Valid when all fields are provided and valid`() {
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name", regionalZone = "North", longitude = 35.0, latitude = 31.0)

        val result = validator(input)

        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `returns single violation when nothing is provided to update`() {
        val input = WarehouseUpdateInput(id = "WH-001")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
    }

    @Test
    fun `returns NoFieldsProvided violation type when nothing is provided to update`() {
        val input = WarehouseUpdateInput(id = "WH-001")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first()).isInstanceOf(ValidatorError.NoFieldsProvided::class.java)
    }

    @Test
    fun `sets ENTITY field in violation when nothing is provided to update`() {
        val input = WarehouseUpdateInput(id = "WH-001")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first().field).isEqualTo(ValidatorField.ENTITY)
    }

    @Test
    fun `returns single violation when id is invalid`() {
        val input = WarehouseUpdateInput(id = "bad-id", name = "New Name")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
    }

    @Test
    fun `returns InvalidIdFormat violation type when id is invalid`() {
        val input = WarehouseUpdateInput(id = "bad-id", name = "New Name")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first()).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }

    @Test
    fun `sets ID field in violation when id is invalid`() {
        val input = WarehouseUpdateInput(id = "bad-id", name = "New Name")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first().field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `returns single violation when provided name is blank`() {
        val input = WarehouseUpdateInput(id = "WH-001", name = " ")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
    }

    @Test
    fun `returns Blank violation type when provided name is blank`() {
        val input = WarehouseUpdateInput(id = "WH-001", name = " ")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first()).isInstanceOf(ValidatorError.Blank::class.java)
    }

    @Test
    fun `sets NAME field in violation when provided name is blank`() {
        val input = WarehouseUpdateInput(id = "WH-001", name = " ")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first().field).isEqualTo(ValidatorField.NAME)
    }

    @Test
    fun `returns single violation when provided regional zone is blank`() {
        val input = WarehouseUpdateInput(id = "WH-001", regionalZone = "")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
    }

    @Test
    fun `returns Blank violation type when provided regional zone is blank`() {
        val input = WarehouseUpdateInput(id = "WH-001", regionalZone = "")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first()).isInstanceOf(ValidatorError.Blank::class.java)
    }

    @Test
    fun `sets REGIONAL_ZONE field in violation when provided regional zone is blank`() {
        val input = WarehouseUpdateInput(id = "WH-001", regionalZone = "")

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first().field).isEqualTo(ValidatorField.REGIONAL_ZONE)
    }

    @Test
    fun `returns single violation when latitude is out of range`() {
        val input = WarehouseUpdateInput(id = "WH-001", latitude = 91.0)

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.OutOfRange::class.java)
        assertThat(violations.first().field).isEqualTo(ValidatorField.LATITUDE)
    }

    @Test
    fun `returns OutOfRange violation type when latitude is out of range`() {
        val input = WarehouseUpdateInput(id = "WH-001", latitude = 91.0)

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.OutOfRange::class.java)
        assertThat(violations.first().field).isEqualTo(ValidatorField.LATITUDE)
    }

    @Test
    fun `sets LATITUDE field in violation when latitude is out of range`() {
        val input = WarehouseUpdateInput(id = "WH-001", latitude = 91.0)

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.OutOfRange::class.java)
        assertThat(violations.first().field).isEqualTo(ValidatorField.LATITUDE)
    }

    @Test
    fun `returns single violation when longitude is out of range`() {
        val input = WarehouseUpdateInput(id = "WH-001", longitude = 181.0)

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
    }

    @Test
    fun `returns OutOfRange violation type when longitude is out of range`() {
        val input = WarehouseUpdateInput(id = "WH-001", longitude = 181.0)

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first()).isInstanceOf(ValidatorError.OutOfRange::class.java)
    }

    @Test
    fun `sets LONGITUDE field in violation when longitude is out of range`() {
        val input = WarehouseUpdateInput(id = "WH-001", longitude = 181.0)

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.first().field).isEqualTo(ValidatorField.LONGITUDE)
    }

    @Test
    fun `returns all violations when several inputs are invalid`() {
        val input = WarehouseUpdateInput(id = "bad", name = "", regionalZone = " ", longitude = 500.0, latitude = -100.0)

        val result = validator(input)

        val violations = violationsOf(result)
        assertThat(violations.map { it.field }).containsExactly(
            ValidatorField.ID, ValidatorField.NAME, ValidatorField.REGIONAL_ZONE, ValidatorField.LATITUDE, ValidatorField.LONGITUDE)
    }
}