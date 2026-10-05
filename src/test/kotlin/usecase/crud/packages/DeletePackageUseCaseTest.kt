package usecase.crud.packages

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.usecase.crud.packages.DeletePackageUseCase
import org.bytebloom.domain.validator.id.PackageIdValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DeletePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val validator = mockk<PackageIdValidator>()

    private val useCase = DeletePackageUseCase(
        packageRepository = packageRepository,
        validator = validator
    )

    @Test
    fun `deletes package successfully`() = runTest {
        // Given
        val id = "PKG-001"

        every { validator(id) } returns ValidatorResult.Valid
        coEvery { packageRepository.delete(id) } returns true

        // When
        val result = useCase(id)

        // Then
        assertTrue(result)

        coVerify(exactly = 1) {
            packageRepository.delete(id)
        }
    }

    @Test
    fun `returns false when package cannot be deleted`() = runTest {
        // Given
        val id = "PKG-404"

        every { validator(id) } returns ValidatorResult.Valid
        coEvery { packageRepository.delete(id) } returns false

        // When
        val result = useCase(id)

        // Then
        assertFalse(result)

        coVerify(exactly = 1) {
            packageRepository.delete(id)
        }
    }

    @Test
    fun `throws validation exception when package id is invalid`() = runTest {
        // Given
        val invalidId = "WH-001"

        every {
            validator(invalidId)
        } returns ValidatorResult.Invalid(
            listOf(
                ValidatorError.InvalidIdFormat(
                    field = ValidatorField.ID,
                    entityType = EntityType.PACKAGE
                )
            )
        )

        // When
        val exception = assertFailsWith<EntityValidationException> {
            useCase(invalidId)
        }

        // Then
        assertEquals(
            ValidatorField.ID,
            exception.violations.single().field
        )

        coVerify(exactly = 0) {
            packageRepository.delete(invalidId)
        }
    }
}