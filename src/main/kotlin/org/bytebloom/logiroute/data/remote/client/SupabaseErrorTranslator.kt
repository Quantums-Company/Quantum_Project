package org.bytebloom.logiroute.data.remote.client

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.domain.model.exception.DatabaseConflictException
import org.bytebloom.logiroute.domain.model.exception.DomainException
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.exception.UnknownDataException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField

class SupabaseErrorTranslator {

    @Suppress("TooGenericExceptionCaught")
    suspend fun <T> translate(
        operation: String,
        block: suspend () -> T
    ): T = try {
        block()
    } catch (e: Exception) {
        throw translateThrowable(e, operation)
    }

    private fun translateThrowable(e: Exception, operation: String): Throwable = when (e) {
        is CancellationException -> e
        is DomainException -> e
        is HttpRequestException, is IOException ->
            NetworkUnavailableException("Network unavailable during $operation", e)
        is RestException -> translateRestException(e, operation)
        else -> UnknownDataException("Unexpected error during $operation: ${e.message}", e)
    }

    private fun translateRestException(e: RestException, operation: String): DomainException =
        when (e.statusCode) {
            HTTP_NOT_FOUND -> ResourceNotFoundException("Resource not found during $operation")
            HTTP_CONFLICT -> DatabaseConflictException("Database conflict during $operation", e)
            HTTP_BAD_REQUEST, HTTP_UNPROCESSABLE_ENTITY -> EntityValidationException(
                violations = listOf(
                    ValidatorError.Custom(
                        field = ValidatorField.ENTITY,
                        message = e.message ?: "Invalid data during $operation"
                    )
                )
            )
            else -> UnknownDataException("Unexpected Supabase error during $operation: ${e.message}", e)
        }

    private companion object {
        private const val HTTP_BAD_REQUEST = 400
        private const val HTTP_NOT_FOUND = 404
        private const val HTTP_CONFLICT = 409
        private const val HTTP_UNPROCESSABLE_ENTITY = 422
    }
}