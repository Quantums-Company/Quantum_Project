package validator.id

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.RouteIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RouteIdValidatorTest {

    private lateinit var validateRouteId: RouteIdValidator

    @BeforeEach
    fun setUp() {
        validateRouteId = RouteIdValidator()
    }

    @ParameterizedTest
    @ValueSource(strings = ["RT-001", "RT-123456", "RT-550e8400-e29b-41d4-a716-446655440000"])
    fun `should return valid when route id has correct prefix and format`(routeId: String) {
        // Given: routeId from @ValueSource

        // When
        val result = validateRouteId(routeId)

        // Then
        assertThat(result).isEqualTo(ValidatorResult.Valid)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   "])
    fun `should return blank violation when route id is blank`(routeId: String) {
        // Given: routeId from @ValueSource

        // When
        val violation = validateRouteId(routeId).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.Blank::class.java)
        assertThat(violation.field).isEqualTo(ValidatorField.ID)
    }

    @ParameterizedTest
    @ValueSource(strings = ["RT-12", "WH-001", "rt-001", "RT-00A", "RT001", "RT-001 "])
    fun `should return invalid format violation when route id is malformed`(routeId: String) {
        // Given: routeId from @ValueSource

        // When
        val violation = validateRouteId(routeId).singleViolation()

        // Then
        assertThat(violation).isInstanceOf(ValidatorError.InvalidIdFormat::class.java)
        assertThat((violation as ValidatorError.InvalidIdFormat).entityType).isEqualTo(EntityType.ROUTE)
    }

    private fun ValidatorResult.singleViolation(): ValidatorError =
        (this as ValidatorResult.Invalid).violations.single()
}