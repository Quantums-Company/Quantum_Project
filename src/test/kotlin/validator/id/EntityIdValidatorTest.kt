package validator.id

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.EntityIdValidator

class EntityIdValidatorTest {
    private val validator = EntityIdValidator()

    private fun violationsOf(result: ValidatorResult): List<ValidatorError> {
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)
        return (result as ValidatorResult.Invalid).violations
    }

    @Test
    fun `validate returns null for three digits sequential id`() {
        val threeDigitsId = "WH-001"

        val threeDigitsResult = validator.validate(threeDigitsId, EntityType.WAREHOUSE)

        assertThat(threeDigitsResult).isNull()
    }

    @Test
    fun `validate returns null for five digits sequential id`() {
        val fiveDigitsId = "WH-12345"

        val fiveDigitsResult = validator.validate(fiveDigitsId, EntityType.WAREHOUSE)

        assertThat(fiveDigitsResult).isNull()
    }

    @Test
    fun `validate returns null for a UUID id`() {
        val id = "WH-123e4567-e89b-12d3-a456-426614174000"

        val result = validator.validate(id, EntityType.WAREHOUSE)

        assertThat(result).isNull()
    }

    @Test
    fun `validate returns Blank error type when id is blank`() {
        val id = "   "

        val result = validator.validate(id, EntityType.WAREHOUSE)

        assertThat(result).isInstanceOf(ValidatorError.Blank::class.java)
    }

    @Test
    fun `validate returns sets ID field in Blank error when id is blank`() {
        val id = "   "

        val result = validator.validate(id, EntityType.WAREHOUSE)

        assertThat(result?.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `validate returns InvalidIdFormat error type when prefix belongs to another entity`() {
        val id = "PKG-001"

        val result = validator.validate(id, EntityType.WAREHOUSE)

        assertThat(result).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }

    @Test
    fun `validate sets expected entityType in error when prefix belongs to another entity`() {
        val id = "PKG-001"

        val result = validator.validate(id, EntityType.WAREHOUSE) as ValidatorError.InvalidIdFormat

        assertThat(result.entityType).isEqualTo(EntityType.WAREHOUSE)
    }

    @Test
    fun `validate sets ID field in error when prefix belongs to another entity`() {
        val id = "PKG-001"

        val result = validator.validate(id, EntityType.WAREHOUSE) as ValidatorError.InvalidIdFormat

        assertThat(result.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `validate returns InvalidIdFormat error for malformed ids`() {
        val malformedIds = listOf("WH-", "WH-01", "WH-abc", "001", "WH-001x", "WH-123e4567-e89b-12d3-a456")

        val results = malformedIds.map { id -> validator.validate(id, EntityType.WAREHOUSE) }

        results.forEach { result -> assertThat(result).isInstanceOf(ValidatorError.InvalidIdFormat::class.java) }
    }

    @Test
    fun `validate accepts the prefix of warehouse Id`() {
        val warehouseId = "WH-001"

        val warehouseResult = validator.validate(warehouseId, EntityType.WAREHOUSE)

        assertThat(warehouseResult).isNull()
    }

    @Test
    fun `validate accepts the prefix of package Id`() {
        val packageId = "PKG-001"

        val packageResult = validator.validate(packageId, EntityType.PACKAGE)

        assertThat(packageResult).isNull()
    }

    @Test
    fun `validate accepts the prefix of route Id`() {
        val routeId = "RT-001"

        val routeResult = validator.validate(routeId, EntityType.ROUTE)

        assertThat(routeResult).isNull()
    }

    @Test
    fun `validate accepts the prefix of vehicle Id`() {
        val vehicleId = "TRK-001"

        val vehicleResult = validator.validate(vehicleId, EntityType.VEHICLE)

        assertThat(vehicleResult).isNull()
    }

    @Test
    fun `invoke returns Valid for a correct id`() {
        val id = "WH-001"

        val result = validator(id, EntityType.WAREHOUSE)

        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `invoke returns single violation for an incorrect id`() {
        val id = "bad-id"

        val result = validator(id, EntityType.WAREHOUSE)

        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
    }

    @Test
    fun `invoke returns InvalidIdFormat violation type for an incorrect id`() {
        val id = "bad-id"

        val result = validator(id, EntityType.WAREHOUSE)

        val violations = violationsOf(result)
        assertThat(violations.first()).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }
}