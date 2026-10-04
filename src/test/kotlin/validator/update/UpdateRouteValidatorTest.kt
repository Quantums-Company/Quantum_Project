package validator.update

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.input.update.RouteUpdateInput
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.update.UpdateRouteValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UpdateRouteValidatorTest {

    private lateinit var validateRouteUpdate: UpdateRouteValidator

    @BeforeEach
    fun setUp() {
        validateRouteUpdate = UpdateRouteValidator()
    }

    @Test
    fun `should return valid when id is correct and one field is updated`() {
        // Given
        val input = RouteUpdateInput(id = VALID_ROUTE_ID, distanceKm = 120.0)

        // When
        val result = validateRouteUpdate(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return valid when all fields are updated with correct values`() {
        // Given
        val input = RouteUpdateInput(
            id = VALID_ROUTE_ID,
            distanceKm = 120.0,
            typicalDelayMin = 15,
            originWarehouse = createWarehouse(id = "WH-001"),
            destinationWarehouse = createWarehouse(id = "WH-002")
        )

        // When
        val result = validateRouteUpdate(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should accept zero typical delay`() {
        // Given
        val input = RouteUpdateInput(id = VALID_ROUTE_ID, typicalDelayMin = 0)

        // When
        val result = validateRouteUpdate(input)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return no fields violation when only id is provided`() {
        // Given
        val input = RouteUpdateInput(id = VALID_ROUTE_ID)

        // When
        val violation = validateRouteUpdate(input).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.NoFieldsProvided::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.ENTITY)
    }

    @Test
    fun `should return invalid format violation when route id is malformed`() {
        // Given
        val input = RouteUpdateInput(id = "WH-001", distanceKm = 120.0)

        // When
        val violation = validateRouteUpdate(input).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.ID)
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, -5.0])
    fun `should return not positive violation when distance is zero or negative`(distanceKm: Double) {
        // Given
        val input = RouteUpdateInput(id = VALID_ROUTE_ID, distanceKm = distanceKm)

        // When
        val violation = validateRouteUpdate(input).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.NotPositive::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.DISTANCE_KM)
    }

    @ParameterizedTest
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY])
    fun `should return non finite violation when distance is not a finite number`(distanceKm: Double) {
        // Given
        val input = RouteUpdateInput(id = VALID_ROUTE_ID, distanceKm = distanceKm)

        // When
        val violation = validateRouteUpdate(input).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.DISTANCE_KM)
    }

    @Test
    fun `should return negative value violation when typical delay is negative`() {
        // Given
        val input = RouteUpdateInput(id = VALID_ROUTE_ID, typicalDelayMin = -1)

        // When
        val violation = validateRouteUpdate(input).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.NegativeValue::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.TYPICAL_DELAY_MIN)
    }

    @Test
    fun `should return blank violation when origin warehouse id is blank`() {
        // Given
        val input = RouteUpdateInput(id = VALID_ROUTE_ID, originWarehouse = warehouseWithBlankId())

        // When
        val violation = validateRouteUpdate(input).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.ORIGIN_WAREHOUSE)
    }

    @Test
    fun `should return blank violation when destination warehouse id is blank`() {
        // Given
        val input = RouteUpdateInput(id = VALID_ROUTE_ID, destinationWarehouse = warehouseWithBlankId())

        // When
        val violation = validateRouteUpdate(input).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.DESTINATION_WAREHOUSE)
    }

    @Test
    fun `should collect all violations when multiple fields are invalid`() {
        // Given
        val input = RouteUpdateInput(id = "WH-001", distanceKm = 0.0, typicalDelayMin = -1)

        // When
        val result = validateRouteUpdate(input)

        // Then
        val violationTypes = (result as ValidatorResult.Invalid).violations.map { it::class }
        assertThat(violationTypes).containsExactly(
            ValidatorError.InvalidIdFormat::class,
            ValidatorError.NotPositive::class,
            ValidatorError.NegativeValue::class
        )
    }

    private fun ValidatorResult.singleViolation(): ValidatorError =
        (this as ValidatorResult.Invalid).violations.single()

    private fun warehouseWithBlankId(): Warehouse = mockk { every { id } returns " " }

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