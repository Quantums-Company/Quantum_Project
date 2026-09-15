package org.bytebloom.data.remote.client

data class SupabaseConfig(
    val projectUrl: String,
    val apiKey: String
) {
    val restUrl: String
        get() = projectUrl.trimEnd('/') + "/rest/v1"
}