package com.samsung.verto

import com.samsung.verto.domain.model.AuthResult
import com.samsung.verto.domain.model.Balance
import com.samsung.verto.domain.model.Transaction
import com.samsung.verto.domain.model.User
import com.samsung.verto.domain.repository.PaymentRepository

class FakePaymentRepository : PaymentRepository {
    var authResult: AuthResult = AuthResult(
        user = User(1, "demo@example.com"),
        accessToken = "token",
        expiresAt = "2099-01-01T00:00:00Z",
    )
    var user: User = authResult.user
    var balance: Balance = Balance(userId = 1, amount = 0, currency = "RUB")
    var transactions: List<Transaction> = emptyList()
    var searchResults: List<User> = listOf(user)

    override suspend fun auth(password: String, email: String, displayName: String?, register: Boolean): AuthResult = authResult

    override suspend fun getUser(): User = user

    override suspend fun getBalance(): Balance = balance

    override suspend fun topup(amount: Long): Transaction {
        balance = balance.copy(amount = balance.amount + amount)
        val tx = Transaction(1, null, user.id, amount, "TOPUP", "now")
        transactions = listOf(tx) + transactions
        return tx
    }

    override suspend fun transfer(toUserId: Long, amount: Long): Transaction {
        balance = balance.copy(amount = balance.amount - amount)
        val tx = Transaction(2, user.id, toUserId, amount, "TRANSFER", "now")
        transactions = listOf(tx) + transactions
        return tx
    }

    override suspend fun getTransactions(limit: Int, offset: Long): List<Transaction> = transactions

    override suspend fun searchUsers(query: String): List<User> = searchResults
}
