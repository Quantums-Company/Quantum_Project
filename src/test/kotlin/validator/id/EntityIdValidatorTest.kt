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
    fun `validate returns null for a sequential id`() {
        // Given
        val threeDigitsId = "WH-001"
        val fiveDigitsId = "WH-12345"

        // When
        val threeDigitsResult = validator.validate(threeDigitsId, EntityType.WAREHOUSE)
        val fiveDigitsResult = validator.validate(fiveDigitsId, EntityType.WAREHOUSE)

        // Then
        assertThat(threeDigitsResult).isNull()
        assertThat(fiveDigitsResult).isNull()
    }

    @Test
    fun `validate returns null for a UUID id`() {
        // Given
        val id = "WH-123e4567-e89b-12d3-a456-426614174000"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `validate returns Blank error when id is blank`() {
        // Given
        val id = "   "

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(result?.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `validate returns InvalidIdFormat error when prefix belongs to another entity`() {
        // Given
        val id = "PKG-001"

        // When
        val result = validator.validate(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
        val error = result as ValidatorError.InvalidIdFormat
        assertThat(error.entityType).isEqualTo(EntityType.WAREHOUSE)
        assertThat(error.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `validate returns InvalidIdFormat error for malformed ids`() {
        // Given
        val malformedIds = listOf("WH-", "WH-01", "WH-abc", "001", "WH-001x", "WH-123e4567-e89b-12d3-a456")

        // When
        val results = malformedIds.map { id -> validator.validate(id, EntityType.WAREHOUSE) }

        // Then
        results.forEach { result -> assertThat(result).isInstanceOf(ValidatorError.InvalidIdFormat::class.java) }
    }

    @Test
    fun `validate accepts the prefix of each entity type`() {
        // Given
        val warehouseId = "WH-001"
        val packageId = "PKG-001"
        val routeId = "RT-001"
        val vehicleId = "TRK-001"

        // When
        val warehouseResult = validator.validate(warehouseId, EntityType.WAREHOUSE)
        val packageResult = validator.validate(packageId, EntityType.PACKAGE)
        val routeResult = validator.validate(routeId, EntityType.ROUTE)
        val vehicleResult = validator.validate(vehicleId, EntityType.VEHICLE)

        // Then
        assertThat(warehouseResult).isNull()
        assertThat(packageResult).isNull()
        assertThat(routeResult).isNull()
        assertThat(vehicleResult).isNull()
    }

    @Test
    fun `invoke returns Valid for a correct id`() {
        // Given
        val id = "WH-001"

        // When
        val result = validator(id, EntityType.WAREHOUSE)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `invoke returns Invalid with one violation for an incorrect id`() {
        // Given
        val id = "bad-id"

        // When
        val result = validator(id, EntityType.WAREHOUSE)

        // Then
        val violations = violationsOf(result)
        assertThat(violations).hasSize(1)
        assertThat(violations.first()).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
    }
}