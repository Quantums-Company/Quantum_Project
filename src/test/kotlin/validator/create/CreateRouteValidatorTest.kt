package validator.create

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.create.CreateRouteValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CreateRouteValidatorTest {

    private lateinit var validateNewRoute: CreateRouteValidator

    @BeforeEach
    fun setUp() {
        validateNewRoute = CreateRouteValidator()
    }

    @Test
    fun `should return valid when route has correct values`() {
        // Given
        val route = Route(
            id = "RT-001",
            distanceKm = 120.0,
            typicalDelayMin = 15,
            originWarehouse = createWarehouse(id = "WH-001"),
            destinationWarehouse = createWarehouse(id = "WH-002")
        )

        // When
        val result = validateNewRoute(route)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should accept zero typical delay`() {
        // Given
        val route = mockRoute(delayMinutes = 0)

        // When
        val result = validateNewRoute(route)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @Test
    fun `should return blank violation when route id is blank`() {
        // Given
        val route = mockRoute(routeId = "")

        // When
        val violation = validateNewRoute(route).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.ID)
    }

    @Test
    fun `should return invalid format violation when route id has wrong prefix`() {
        // Given
        val route = mockRoute(routeId = "WH-001")

        // When
        val violation = validateNewRoute(route).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.ID)
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, -10.0])
    fun `should return not positive violation when distance is zero or negative`(distance: Double) {
        // Given
        val route = mockRoute(distance = distance)

        // When
        val violation = validateNewRoute(route).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.NotPositive::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.DISTANCE_KM)
    }

    @ParameterizedTest
    @ValueSource(doubles = [Double.NaN, Double.POSITIVE_INFINITY])
    fun `should return non finite violation when distance is not a finite number`(distance: Double) {
        // Given
        val route = mockRoute(distance = distance)

        // When
        val violation = validateNewRoute(route).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.NonFiniteNumber::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.DISTANCE_KM)
    }

    @Test
    fun `should return negative value violation when typical delay is negative`() {
        // Given
        val route = mockRoute(delayMinutes = -1)

        // When
        val violation = validateNewRoute(route).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.NegativeValue::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.TYPICAL_DELAY_MIN)
    }

    @Test
    fun `should return blank violation when origin warehouse id is blank`() {
        // Given
        val route = mockRoute(originId = " ")

        // When
        val violation = validateNewRoute(route).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.ORIGIN_WAREHOUSE)
    }

    @Test
    fun `should return blank violation when destination warehouse id is blank`() {
        // Given
        val route = mockRoute(destinationId = " ")

        // When
        val violation = validateNewRoute(route).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.DESTINATION_WAREHOUSE)
    }

    @Test
    fun `should collect all violations when multiple fields are invalid`() {
        // Given
        val route = mockRoute(routeId = "", distance = 0.0, delayMinutes = -1)

        // When
        val result = validateNewRoute(route)

        // Then
        val violationTypes = (result as ValidatorResult.Invalid).violations.map { it::class }
        assertThat(violationTypes).containsExactly(
            ValidatorError.Blank::class,
            ValidatorError.NotPositive::class,
            ValidatorError.NegativeValue::class
        )
    }

    private fun ValidatorResult.singleViolation(): ValidatorError =
        (this as ValidatorResult.Invalid).violations.single()

    private fun mockRoute(
        routeId: String = "RT-001",
        distance: Double = 120.0,
        delayMinutes: Int = 15,
        originId: String = "WH-001",
        destinationId: String = "WH-002"
    ): Route {
        val origin = mockWarehouse(originId)
        val destination = mockWarehouse(destinationId)
        return mockk {
            every { id } returns routeId
            every { distanceKm } returns distance
            every { typicalDelayMin } returns delayMinutes
            every { originWarehouse } returns origin
            every { destinationWarehouse } returns destination
        }
    }

    private fun mockWarehouse(warehouseId: String): Warehouse = mockk { every { id } returns warehouseId }

    private fun createWarehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "West Bank",
        longitude = 35.2,
        latitude = 31.9
    )
}