package org.bytebloom.data.remote.client

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import kotlin.time.Duration.Companion.seconds

object SupabaseClientProvider {
    private val supabaseUrl = System.getenv("URL") ?: ""
    private val supabaseKey = System.getenv("Key") ?: ""

    private val CONNECT_TIMEOUT = 3.seconds
    private val REQUEST_TIMEOUT = 5.seconds

    fun create(): SupabaseClient =
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            httpEngine = CIO.create {
                endpoint {
                    connectTimeout = CONNECT_TIMEOUT.inWholeMilliseconds
                    requestTimeout = REQUEST_TIMEOUT.inWholeMilliseconds
                }
            }

            install(Postgrest)
        }
}