package org.bytebloom.data.remote.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class SupabaseHttpClient(
    val config: SupabaseConfig
) {
    @PublishedApi
    internal val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                }
            )
        }
        defaultRequest {
            header("apikey", config.apiKey)
            header(HttpHeaders.Authorization, "Bearer ${config.apiKey}")
            accept(ContentType.Application.Json)
        }
    }

    suspend fun getTableJson(table: String): String {
        return httpClient
            .get("${config.restUrl}/$table")
            .bodyAsText()
    }

    suspend inline fun <reified T> insert(
        table: String,
        data: T
    ): HttpResponse {
        return httpClient
            .post("${config.restUrl}/$table") {
                contentType(ContentType.Application.Json)
                header("Prefer", "return=minimal")
                setBody(data)
            }
    }

    fun close() {
        httpClient.close()
    }
}