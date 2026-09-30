package validator.update

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.updateInput.PackageUpdateInput
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.update.UpdatePackageValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertIs

class UpdatePackageValidatorTest {

    @Test
    fun `rejects identical origin and destination warehouses`() {
        val validator = UpdatePackageValidator()
        val warehouse = Warehouse(
            id = "WH-001",
            name = "Main Hub",
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )
        val input = PackageUpdateInput(
            id = "PKG-001",
            originWarehouse = warehouse,
            destinationWarehouse = warehouse
        )

        val result = validator(input)

        val invalid = assertIs<ValidatorResult.Invalid>(result)
        assertIs<ValidatorError.SameWarehouse>(
            invalid.violations.single()
        )
    }
}
