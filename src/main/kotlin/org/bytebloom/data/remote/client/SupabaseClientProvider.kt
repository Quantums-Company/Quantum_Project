package org.bytebloom.data.remote.client

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.SupabaseClient

object SupabaseClientProvider {
    private val supabaseUrl = System.getenv("URL") ?: ""
    private val supabaseKey = System.getenv("Key") ?: ""

    fun create(): SupabaseClient =
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Postgrest)
        }
}