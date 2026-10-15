package org.bytebloom.data.remote.client

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import kotlin.time.Duration.Companion.seconds

class SupabaseClientProvider {
    private val supabaseUrl = System.getenv("URL") ?: ""
    private val supabaseKey = System.getenv("Key") ?: ""

    private val connectTimeout = 3.seconds
    private val requestTimeout = 5.seconds

    fun create(): SupabaseClient =
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            httpEngine = CIO.create {
                endpoint {
                    connectTimeout = this@SupabaseClientProvider.connectTimeout.inWholeMilliseconds
                    requestTimeout = this@SupabaseClientProvider.requestTimeout.inWholeMilliseconds
                }
            }

            install(Postgrest)
        }
}