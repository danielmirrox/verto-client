package com.samsung.verto.domain.repository

import com.samsung.verto.domain.model.AuthResult
import com.samsung.verto.domain.model.Balance
import com.samsung.verto.domain.model.Transaction
import com.samsung.verto.domain.model.User

interface PaymentRepository {
    suspend fun auth(password: String, email: String, displayName: String?, register: Boolean): AuthResult
    suspend fun getUser(): User
    suspend fun getBalance(): Balance
    suspend fun topup(amount: Long): Transaction
    suspend fun transfer(toUserId: Long, amount: Long): Transaction
    suspend fun getTransactions(limit: Int, offset: Long): List<Transaction>
    suspend fun searchUsers(query: String): List<User>
}
