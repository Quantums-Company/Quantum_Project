package org.bytebloom.logiroute.data.remote.util

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.model.exception.UnknownDataException
import kotlin.test.Test

class RetryUtilsTest {

    @Test
    fun `should retry with exponential backoff when network is temporarily unavailable`() = runTest {
        // Given
        var attempts = 0

        // When
        val result = retryWithBackoff {
            attempts++
            if (attempts <= FAILURES_BEFORE_SUCCESS) {
                throw NetworkUnavailableException("Temporary network failure")
            }
            SUCCESS_VALUE
        }

        // Then
        assertThat(result.getOrThrow()).isEqualTo(SUCCESS_VALUE)
        assertThat(attempts).isEqualTo(FAILURES_BEFORE_SUCCESS + 1)
    }

    @Test
    fun `should not retry when failure is not retryable`() = runTest {
        // Given
        var attempts = 0

        // When
        val result = retryWithBackoff {
            attempts++
            throw UnknownDataException("Non-retryable error")
        }

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(attempts).isEqualTo(1)
    }

    private companion object {
        const val SUCCESS_VALUE = "success"
        const val FAILURES_BEFORE_SUCCESS = 3
    }
}