package org.bytebloom.data.remote.client

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import org.bytebloom.domain.exception.DatabaseConflictException
import org.bytebloom.domain.exception.DomainException
import org.bytebloom.domain.exception.EntityValidationException
import org.bytebloom.domain.exception.NetworkUnavailableException
import org.bytebloom.domain.exception.ResourceNotFoundException
import org.bytebloom.domain.exception.UnknownDataException

object SupabaseErrorTranslator {

    suspend fun <T> translate(
        operation: String,
        block: suspend () -> T
    ): T {
        try {
            return block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: DomainException) {
            throw e
        } catch (e: HttpRequestException) {
            throw NetworkUnavailableException(
                message = "Network unavailable during $operation",
                cause = e
            )
        } catch (e: IOException) {
            throw NetworkUnavailableException(
                message = "Network unavailable during $operation",
                cause = e
            )
        } catch (e: RestException) {
            when (e.statusCode) {
                404 -> throw ResourceNotFoundException(
                    "Resource not found during $operation"
                )
                409 -> throw DatabaseConflictException(
                    message = "Database conflict during $operation",
                    cause = e
                )
                400, 422 -> throw EntityValidationException(
                    listOf(e.message ?: "Invalid data during $operation")
                )
                else -> throw UnknownDataException(
                    message = "Unexpected Supabase error during $operation: ${e.message}",
                    cause = e
                )
            }
        } catch (e: Exception) {
            throw UnknownDataException(
                message = "Unexpected error during $operation: ${e.message}",
                cause = e
            )
        }
    }
}