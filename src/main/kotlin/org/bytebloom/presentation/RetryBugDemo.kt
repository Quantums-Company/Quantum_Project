package org.bytebloom.scratch

import kotlinx.coroutines.runBlocking
import org.bytebloom.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.util.retryWithBackoff
import java.io.IOException

fun main() = runBlocking {
    println("=== TEST: New order (translate INSIDE retryWithBackoff) ===")
    var attempts = 0

    val result = retryWithBackoff<String> {
        SupabaseErrorTranslator.translate("simulated operation") {
            attempts++
            println("Attempt #$attempts")
            throw IOException("Simulated network failure")
        }
    }

    println("Total attempts: $attempts")
    println("Result: $result")
}