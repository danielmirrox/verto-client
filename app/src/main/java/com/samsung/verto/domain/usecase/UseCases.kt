package com.samsung.verto.domain.usecase

import com.samsung.verto.domain.repository.PaymentRepository

class UseCases(repository: PaymentRepository) {
    val auth = AuthUseCase(repository)
    val getUser = GetUserUseCase(repository)
    val getBalance = GetBalanceUseCase(repository)
    val topup = TopupUseCase(repository)
    val transfer = TransferUseCase(repository)
    val getTransactions = GetTransactionsUseCase(repository)
    val searchUsers = SearchUsersUseCase(repository)
}

class AuthUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(password: String, email: String, displayName: String?, register: Boolean = false) =
        repository.auth(password, email, displayName, register)
}

class GetUserUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke() = repository.getUser()
}

class GetBalanceUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke() = repository.getBalance()
}

class TopupUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(amount: Long) = repository.topup(amount)
}

class TransferUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(toUserId: Long, amount: Long) = repository.transfer(toUserId, amount)
}

class GetTransactionsUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(limit: Int, offset: Long) =
        repository.getTransactions(limit, offset)
}

class SearchUsersUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(query: String) = repository.searchUsers(query)
}
