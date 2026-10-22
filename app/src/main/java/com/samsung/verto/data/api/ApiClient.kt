package com.samsung.verto.data.api

import com.samsung.verto.data.local.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient(private val sessionStore: SessionStore) {
    private val client = HttpClient(OkHttp) {
        expectSuccess = true
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                val failure = exception as? ResponseException ?: return@handleResponseExceptionWithRequest
                val error = runCatching { failure.response.body<ErrorResponseDto>() }.getOrNull()
                val message = when (error?.code) {
                    "EMAIL_EXISTS" -> "Эта почта уже зарегистрирована. Войдите в аккаунт."
                    "UNAUTHORIZED" -> "Неверная почта или пароль, либо срок сеанса истёк."
                    "INSUFFICIENT_FUNDS" -> "Недостаточно средств для перевода."
                    "NOT_FOUND" -> "Получатель не найден. Проверьте его ID."
                    "AMOUNT_OVERFLOW" -> "Сумма превышает допустимый предел."
                    "INVALID_REQUEST" -> "Проверьте введённые данные и допустимость операции."
                    else -> "Ошибка сервера. Повторите попытку позже."
                }
                throw IllegalStateException(message)
            }
        }
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
        client.post("auth") { setBody(request) }.body()

    suspend fun getUser(): UserResponseDto = client.get("user").body()

    suspend fun getBalance(): BalanceResponseDto = client.get("balance").body()

    suspend fun topup(request: TopupRequestDto): TransactionResponseDto =
        client.post("topup") { setBody(request) }.body()

    suspend fun transfer(request: TransferRequestDto): TransactionResponseDto =
        client.post("transfer") { setBody(request) }.body()

    suspend fun getTransactions(limit: Int, offset: Long): TransactionsResponseDto =
        client.get("transactions") { parameter("limit", limit); parameter("offset", offset) }.body()

    suspend fun searchUsers(query: String): UsersResponseDto =
        client.get("users/search") { parameter("q", query) }.body()
}
