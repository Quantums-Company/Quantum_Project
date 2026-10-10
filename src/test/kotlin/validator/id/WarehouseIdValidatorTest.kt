package validator.id

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.WarehouseIdValidator

class WarehouseIdValidatorTest {
    private val validator = WarehouseIdValidator()

    private fun violationsOf(result: ValidatorResult): List<ValidatorError> {
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        return (result as ValidatorResult.Invalid).violations }

    @Test
    fun `returns Valid for a sequential warehouse id`() {
        val id = "WH-001"

        val result = validator(id)

        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `returns Valid for a UUID warehouse id`() {
        val id = "WH-123e4567-e89b-12d3-a456-426614174000"

        val result = validator(id)

        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `returns single violation when id is blank`() {
        val id = ""

        val violations = violationsOf(validator(id))

        assertThat(violations).hasSize(1)
    }

    @Test
    fun `returns Blank violation type when id is blank`() {
        val id = ""

        val violations = violationsOf(validator(id))

        assertThat(violations.first()).isInstanceOf(ValidatorError.Blank::class.java)
    }

    @Test
    fun `sets ID field in violation when id is blank`() {
        val id = ""

        val violations = violationsOf(validator(id))

        assertThat(violations.first().field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `returns single violation when id belongs to another entity`() {
        val id = "PKG-001"

        val violations = violationsOf(validator(id))

        assertThat(violations).hasSize(1)
    }

@Test
fun `returns InvalidIdFormat violation type when id belongs to another entity`() {
    val id = "PKG-001"

    val violations = violationsOf(validator(id))

    assertThat(violations.first()).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
}

@Test
fun `sets WAREHOUSE entityType in violation when id belongs to another entity`() {
    val id = "PKG-001"

    val violations = violationsOf(validator(id))
    val error = violations.first() as ValidatorError.InvalidIdFormat

    assertThat(error.entityType).isEqualTo(EntityType.WAREHOUSE)
}

    @Test
    fun `returns Invalid when id length is too short`() {
        val tooShort = "WH-1"

        val short = validator(tooShort)

        assertThat(short).isInstanceOf(ValidatorResult.Invalid::class.java)
    }

    @Test
    fun `returns Invalid when id prefix is wrong`() {
        val wrongPrefix = "warehouse-001"

        val wrong = validator(wrongPrefix)

        assertThat(wrong).isInstanceOf(ValidatorResult.Invalid::class.java)
    }
}