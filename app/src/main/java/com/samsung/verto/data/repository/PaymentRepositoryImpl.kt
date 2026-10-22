package com.samsung.verto.data.repository

import com.samsung.verto.data.api.ApiClient
import com.samsung.verto.data.api.AuthRequestDto
import com.samsung.verto.data.api.TopupRequestDto
import com.samsung.verto.data.api.TransferRequestDto
import com.samsung.verto.data.local.SessionStore
import com.samsung.verto.domain.model.AuthResult
import com.samsung.verto.domain.model.Balance
import com.samsung.verto.domain.model.Transaction
import com.samsung.verto.domain.model.User
import com.samsung.verto.domain.repository.PaymentRepository

class PaymentRepositoryImpl(
    private val apiClient: ApiClient,
    private val sessionStore: SessionStore,
) : PaymentRepository {
    override suspend fun auth(password: String, email: String, displayName: String?, register: Boolean): AuthResult {
        val response = apiClient.auth(AuthRequestDto(password, email, displayName, register))
        sessionStore.accessToken = response.accessToken
        val user = apiClient.getUser()
        return AuthResult(
            user = User(user.id, user.email, user.displayName),
            accessToken = response.accessToken,
            expiresAt = response.expiresAt,
        )
    }

    override suspend fun getUser(): User {
        val response = apiClient.getUser()
        return User(response.id, response.email, response.displayName)
    }

    override suspend fun getBalance(): Balance {
        val response = apiClient.getBalance()
        return Balance(response.userId, response.balance, response.currency)
    }

    override suspend fun topup(amount: Long): Transaction {
        val response = apiClient.topup(TopupRequestDto(amount))
        return Transaction(
            id = response.id,
            fromUserId = response.fromUserId,
            toUserId = response.toUserId,
            amount = response.amount,
            type = response.type,
            createdAt = response.createdAt,
        )
    }

    override suspend fun transfer(toUserId: Long, amount: Long): Transaction {
        val response = apiClient.transfer(TransferRequestDto(toUserId, amount))
        return Transaction(
            id = response.id,
            fromUserId = response.fromUserId,
            toUserId = response.toUserId,
            amount = response.amount,
            type = response.type,
            createdAt = response.createdAt,
        )
    }

    override suspend fun getTransactions(limit: Int, offset: Long): List<Transaction> {
        val response = apiClient.getTransactions(limit, offset)
        return response.items.map {
            Transaction(
                id = it.id,
                fromUserId = it.fromUserId,
                toUserId = it.toUserId,
                amount = it.amount,
                type = it.type,
                createdAt = it.createdAt,
            )
        }
    }

    override suspend fun searchUsers(query: String): List<User> {
        val response = apiClient.searchUsers(query)
        return response.items.map { User(it.id, it.email, it.displayName) }
    }
}
