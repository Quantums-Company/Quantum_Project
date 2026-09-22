package org.bytebloom.data.remote.client

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import org.bytebloom.domain.model.exception.DatabaseConflictException
import org.bytebloom.domain.model.exception.DomainException
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.NetworkUnavailableException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.exception.UnknownDataException
import org.bytebloom.domain.validation.ValidationError
import org.bytebloom.domain.validation.ValidationField

object SupabaseErrorTranslator {

    private const val HTTP_BAD_REQUEST = 400
    private const val HTTP_NOT_FOUND = 404
    private const val HTTP_CONFLICT = 409
    private const val HTTP_UNPROCESSABLE_ENTITY = 422

    @Suppress("TooGenericExceptionCaught")
    suspend fun <T> translate(
        operation: String,
        block: suspend () -> T
    ): T = try {
        block()
    } catch (e: Exception) {
        throw mapThrowable(e, operation)
    }

    private fun mapThrowable(e: Exception, operation: String): Throwable = when (e) {
        is CancellationException -> e
        is DomainException -> e
        is HttpRequestException, is IOException ->
            NetworkUnavailableException("Network unavailable during $operation", e)
        is RestException -> mapRestException(e, operation)
        else -> UnknownDataException("Unexpected error during $operation: ${e.message}", e)
    }

    private fun mapRestException(e: RestException, operation: String): DomainException =
        when (e.statusCode) {
            HTTP_NOT_FOUND -> ResourceNotFoundException("Resource not found during $operation")
            HTTP_CONFLICT -> DatabaseConflictException("Database conflict during $operation", e)
            HTTP_BAD_REQUEST, HTTP_UNPROCESSABLE_ENTITY -> EntityValidationException(
                violations = listOf(
                    ValidationError.Custom(
                        field = ValidationField.Entity(),
                        message = e.message ?: "Invalid data during $operation"
                    )
                )
            )
            else -> UnknownDataException("Unexpected Supabase error during $operation: ${e.message}", e)
        }
}