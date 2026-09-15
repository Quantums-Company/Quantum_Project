package org.bytebloom.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class SupabaseHttpClient(
    private val config: SupabaseConfig
) {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
        defaultRequest {
            header("apikey", config.apiKey)
            header(HttpHeaders.Authorization, "Bearer ${config.apiKey}")
            accept(ContentType.Application.Json)
        }
    }

    fun getTableJson(table: String): String = runBlocking {
        httpClient
            .get("${config.restUrl}/$table")
            .bodyAsText()
    }

    fun close() {
        httpClient.close()
    }
}