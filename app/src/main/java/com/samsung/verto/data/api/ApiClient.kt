package com.samsung.verto.data.api

import com.samsung.verto.data.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient(private val sessionStore: SessionStore) {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        defaultRequest {
            url(ApiConfig.BASE_URL)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            sessionStore.accessToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }

    suspend fun auth(request: AuthRequestDto): AuthResponseDto =
        client.post("/auth") { setBody(request) }.body()

    suspend fun getUser(): UserResponseDto = client.get("/user").body()

    suspend fun getBalance(): BalanceResponseDto = client.get("/balance").body()

    suspend fun topup(request: TopupRequestDto): TransactionResponseDto =
        client.post("/topup") { setBody(request) }.body()

    suspend fun transfer(request: TransferRequestDto): TransactionResponseDto =
        client.post("/transfer") { setBody(request) }.body()

    suspend fun getTransactions(limit: Int, offset: Long): TransactionsResponseDto =
        client.get("/transactions?limit=$limit&offset=$offset").body()

    suspend fun searchUsers(query: String): UsersResponseDto =
        client.get("/users/search?q=$query").body()
}
