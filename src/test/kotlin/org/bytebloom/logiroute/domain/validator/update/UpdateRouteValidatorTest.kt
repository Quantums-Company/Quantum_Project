package org.bytebloom.logiroute.domain.validator.update

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.input.update.RouteUpdateInput
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.update.UpdateRouteValidator
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UpdateRouteValidatorTest {

    private val validator = UpdateRouteValidator()

    @Test
    fun `should return valid when one route field is updated`() {
        // Given
        val input = RouteUpdateInput(
            id = VALID_ROUTE_ID,
            distanceKm = 120.0
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return valid when all route fields are updated correctly`() {
        // Given
        val input = RouteUpdateInput(
            id = VALID_ROUTE_ID,
            distanceKm = 120.0,
            typicalDelayMin = 15,
            originWarehouse = createWarehouse("WH-001"),
            destinationWarehouse = createWarehouse("WH-002")
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return valid when typical delay is zero`() {
        // Given
        val input = RouteUpdateInput(
            id = VALID_ROUTE_ID,
            typicalDelayMin = 0
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return no fields violation when only id is provided`() {
        // Given
        val input = RouteUpdateInput(id = VALID_ROUTE_ID)

        // When
        val result = validator(input)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(
            ValidatorError.NoFieldsProvided::class.java
        )
        assertThat(error.field).isEqualTo(ValidatorField.ENTITY)
    }

    @Test
    fun `should return invalid format violation when route id is malformed`() {
        // Given
        val input = RouteUpdateInput(
            id = "WH-001",
            distanceKm = 120.0
        )

        // When
        val result = validator(input)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(
            ValidatorError.InvalidIdFormat::class.java
        )

        val formatError = error as ValidatorError.InvalidIdFormat
        assertThat(formatError.field).isEqualTo(ValidatorField.ID)
        assertThat(formatError.entityType).isEqualTo(EntityType.ROUTE)
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, -5.0])
    fun `should return not positive violation when distance is zero or negative`(
        distanceKm: Double
    ) {
        // Given
        val input = RouteUpdateInput(
            id = VALID_ROUTE_ID,
            distanceKm = distanceKm
        )

        // When
        val result = validator(input)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(
            ValidatorError.NotPositive::class.java
        )
        assertThat(error.field).isEqualTo(ValidatorField.DISTANCE_KM)
    }

    @ParameterizedTest
    @ValueSource(
        doubles = [Double.NaN, Double.POSITIVE_INFINITY]
    )
    fun `should return non finite violation when distance is not finite`(
        distanceKm: Double
    ) {
        // Given
        val input = RouteUpdateInput(
            id = VALID_ROUTE_ID,
            distanceKm = distanceKm
        )

        // When
        val result = validator(input)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(
            ValidatorError.NonFiniteNumber::class.java
        )
        assertThat(error.field).isEqualTo(ValidatorField.DISTANCE_KM)
    }

    @Test
    fun `should return negative value violation when typical delay is negative`() {
        // Given
        val input = RouteUpdateInput(
            id = VALID_ROUTE_ID,
            typicalDelayMin = -1
        )

        // When
        val result = validator(input)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(
            ValidatorError.NegativeValue::class.java
        )
        assertThat(error.field).isEqualTo(ValidatorField.TYPICAL_DELAY_MIN)
    }

    @Test
    fun `should return blank violation when origin warehouse id is blank`() {
        // Given
        val input = RouteUpdateInput(
            id = VALID_ROUTE_ID,
            originWarehouse = warehouseWithBlankId()
        )

        // When
        val result = validator(input)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.ORIGIN_WAREHOUSE)
    }

    @Test
    fun `should return blank violation when destination warehouse id is blank`() {
        // Given
        val input = RouteUpdateInput(
            id = VALID_ROUTE_ID,
            destinationWarehouse = warehouseWithBlankId()
        )

        // When
        val result = validator(input)

        // Then
        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(1)

        val error = invalid.violations.single()
        assertThat(error).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(error.field).isEqualTo(ValidatorField.DESTINATION_WAREHOUSE)
    }

    @Test
    fun `should collect all violations when multiple fields are invalid`() {
        // Given
        val input = RouteUpdateInput(
            id = "WH-001",
            distanceKm = 0.0,
            typicalDelayMin = -1
        )

        // When
        val result = validator(input)

        // Then
        assertThat(result).isInstanceOf(ValidatorResult.Invalid::class.java)

        val invalid = result as ValidatorResult.Invalid
        assertThat(invalid.violations).hasSize(3)

        val violationTypes = invalid.violations.map { it::class }
        assertThat(violationTypes).containsExactly(
            ValidatorError.InvalidIdFormat::class,
            ValidatorError.NotPositive::class,
            ValidatorError.NegativeValue::class
        )
    }

    private fun warehouseWithBlankId(): Warehouse {
        val warehouse = mockk<Warehouse>()
        every { warehouse.id } returns " "
        return warehouse
    }

    private fun createWarehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "West Bank",
        longitude = 35.2,
        latitude = 31.9
    )

    private companion object {
        const val VALID_ROUTE_ID = "RT-001"
    }
}