package validator.create

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.create.CreateWarehouseValidator

class CreateWarehouseValidatorTest {

    private val validator = CreateWarehouseValidator()

    private fun validWarehouse(): Warehouse {
        val warehouse = mockk<Warehouse>()
        every { warehouse.id } returns "WH-001"
        every { warehouse.name } returns "Main Hub"
        every { warehouse.regionalZone } returns "Central"
        every { warehouse.longitude } returns 35.0
        every { warehouse.latitude } returns 31.0
        return warehouse }

    private fun violationsOf(result: ValidatorResult): List<ValidatorError> {
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        return (result as ValidatorResult.Invalid).violations }

    @Test
    fun `returns Valid when all fields are valid`() {
        // Given
        val warehouse = validWarehouse()

        // When
        val result = validator(warehouse)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `returns Valid when coordinates are exactly on the limits`() {
        // Given
        val warehouse = validWarehouse()
        every { warehouse.latitude } returns 90.0
        every { warehouse.longitude } returns -180.0

        // When
        val result = validator(warehouse)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `returns Blank violation when name is blank`() {
        // Given
        val warehouse = validWarehouse()
        every { warehouse.name } returns "   "

        // When
        val violations = violationsOf(validator(warehouse))

        // Then
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(violations.first().field).isEqualTo(ValidatorField.NAME)
    }

    @Test
    fun `returns Blank violation when regional zone is blank`() {
        // Given
        val warehouse = validWarehouse()
        every { warehouse.regionalZone } returns ""

        // When
        val violations = violationsOf(validator(warehouse))

        // Then
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(violations.first().field).isEqualTo(ValidatorField.REGIONAL_ZONE)
    }

    @Test
    fun `returns OutOfRange violation when latitude is out of range`() {
        // Given
        val warehouse = validWarehouse()
        every { warehouse.latitude } returns 95.0

        // When
        val violations = violationsOf(validator(warehouse))

        // Then
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.OutOfRange::class.java)
        assertThat(violations.first().field).isEqualTo(ValidatorField.LATITUDE)
    }

    @Test
    fun `returns OutOfRange violation when longitude is out of range`() {
        // Given
        val warehouse = validWarehouse()
        every { warehouse.longitude } returns -181.0

        // When
        val violations = violationsOf(validator(warehouse))

        // Then
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.OutOfRange::class.java)
        assertThat(violations.first().field).isEqualTo(ValidatorField.LONGITUDE)
    }

    @Test
    fun `returns InvalidIdFormat violation when id is invalid`() {
        // Given
        val warehouse = validWarehouse()
        every { warehouse.id } returns "XX-1"

        // When
        val violations = violationsOf(validator(warehouse))

        // Then
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
        assertThat(violations.first().field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `returns all violations when several fields are invalid`() {
        // Given
        val warehouse = validWarehouse()
        every { warehouse.name } returns ""
        every { warehouse.regionalZone } returns " "
        every { warehouse.latitude } returns 100.0
        every { warehouse.longitude } returns 200.0

        // When
        val violations = violationsOf(validator(warehouse))

        // Then
        assertThat(violations.map { it.field }).containsExactly(ValidatorField.NAME, ValidatorField.REGIONAL_ZONE,
            ValidatorField.LATITUDE, ValidatorField.LONGITUDE)
    }
}