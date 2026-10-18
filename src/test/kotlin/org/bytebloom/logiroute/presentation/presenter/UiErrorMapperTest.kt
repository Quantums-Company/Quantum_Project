package org.bytebloom.logiroute.presentation.presenter

import org.bytebloom.logiroute.domain.model.exception.DatabaseConflictException
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.presenter.mapper.toUiError
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UiErrorMapperTest {

    @Test
    fun `maps a network failure`() {
        // Given / When
        val error = NetworkUnavailableException("offline").toUiError()

        // Then
        assertEquals(UiError.Network("offline"), error)
    }

    @Test
    fun `maps a missing resource`() {
        // Given / When
        val error = ResourceNotFoundException("no such package").toUiError()

        // Then
        assertEquals(UiError.NotFound("no such package"), error)
    }

    @Test
    fun `maps a conflict`() {
        // Given / When
        val error = DatabaseConflictException("duplicate id").toUiError()

        // Then
        assertEquals(UiError.Conflict("duplicate id"), error)
    }

    @Test
    fun `maps a validation failure`() {
        // Given
        val failure = EntityValidationException(listOf(ValidatorError.Blank(ValidatorField.NAME)))

        // When
        val error = failure.toUiError()

        // Then
        assertEquals(UiError.InvalidInput("Validation failed: Name cannot be blank"), error)
    }

    @Test
    fun `maps anything else to an unexpected error`() {
        // Given / When
        val error = IllegalStateException("boom").toUiError()

        // Then
        assertEquals(UiError.Unexpected("boom"), error)
    }

    @Test
    fun `gives unexpected errors a readable detail even without a message`() {
        // Given / When
        val error = IllegalStateException().toUiError()

        // Then
        assertEquals(UiError.Unexpected("No further details are available."), error)
    }
}
