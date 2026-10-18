package org.bytebloom.logiroute.domain.usecase.packages

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.validator.id.PackageIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFailsWith

class DeletePackageUseCaseTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var deletePackage: DeletePackageUseCase

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        deletePackage = DeletePackageUseCase(packageRepository, PackageIdValidator())
    }

    @Test
    fun `should return true when repository deletes the package`() = runTest {
        // Given
        coEvery { packageRepository.delete(PACKAGE_ID) } returns true

        // When
        val isDeleted = deletePackage(PACKAGE_ID)

        // Then
        assertThat(isDeleted).isTrue()
        coVerify(exactly = 1) { packageRepository.delete(PACKAGE_ID) }
    }

    @Test
    fun `should return false when repository cannot delete the package`() = runTest {
        // Given
        coEvery { packageRepository.delete(MISSING_ID) } returns false

        // When
        val isDeleted = deletePackage(MISSING_ID)

        // Then
        assertThat(isDeleted).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "WH-001", "PKG-12", "pkg-001"])
    fun `should throw validation exception and skip repository when id is invalid`(invalidId: String) = runTest {
        // Given: invalidId from @ValueSource

        // When / Then
        assertFailsWith<EntityValidationException> { deletePackage(invalidId) }
        coVerify(exactly = 0) { packageRepository.delete(any()) }
    }

    @Test
    fun `should propagate network exception when repository fails`() = runTest {
        // Given
        coEvery { packageRepository.delete(PACKAGE_ID) } throws NetworkUnavailableException()

        // When / Then
        assertFailsWith<NetworkUnavailableException> { deletePackage(PACKAGE_ID) }
    }

    private companion object {
        const val PACKAGE_ID = "PKG-001"
        const val MISSING_ID = "PKG-404"
    }
}