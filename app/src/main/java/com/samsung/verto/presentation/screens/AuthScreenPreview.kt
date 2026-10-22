package com.samsung.verto.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.samsung.verto.ui.theme.VertoTheme
import com.samsung.verto.domain.repository.PaymentRepository
import com.samsung.verto.domain.usecase.UseCases
import com.samsung.verto.data.local.InMemorySessionStore
import com.samsung.verto.domain.model.AuthResult
import com.samsung.verto.domain.model.User
import com.samsung.verto.domain.model.Balance
import com.samsung.verto.domain.model.Transaction

private class FakePaymentRepository : PaymentRepository {
    override suspend fun auth(password: String, email: String, displayName: String?, register: Boolean): AuthResult {
        val user = User(id = 1L, email = email, displayName = displayName)
        return AuthResult(user = user, accessToken = "fake-token", expiresAt = "2099-01-01T00:00:00Z")
    }

    override suspend fun getUser(): User = User(id = 1L, email = "demo@verto.app", displayName = "Demo")
    override suspend fun getBalance(): Balance = Balance(userId = 1L, amount = 1500L, currency = "RUB")
    override suspend fun topup(amount: Long): Transaction = Transaction(1L, null, 1L, amount, "topup", "now")
    override suspend fun transfer(toUserId: Long, amount: Long): Transaction = Transaction(2L, 1L, toUserId, amount, "transfer", "now")
    override suspend fun getTransactions(limit: Int, offset: Long): List<Transaction> = listOf()
    override suspend fun searchUsers(query: String): List<User> = listOf()
}

@Preview(name = "AuthScreen Light", showBackground = true)
@Composable
fun AuthScreenPreviewLight() {
    val repo = FakePaymentRepository()
    val useCases = UseCases(repo)
    val session = InMemorySessionStore()
    val vm = com.samsung.verto.presentation.auth.AuthViewModel(useCases, session)
    vm.updateEmail("demo@verto.app")
    vm.updateDisplayName("Daniel")
    vm.updatePassword("password")

    VertoTheme(darkTheme = false) {
        AuthScreen(vm)
    }
}

@Preview(name = "AuthScreen Dark", showBackground = true)
@Composable
fun AuthScreenPreviewDark() {
    val repo = FakePaymentRepository()
    val useCases = UseCases(repo)
    val session = InMemorySessionStore()
    val vm = com.samsung.verto.presentation.auth.AuthViewModel(useCases, session)
    vm.updateEmail("demo@verto.app")
    vm.updateDisplayName("Daniel")
    vm.updatePassword("password")

    VertoTheme(darkTheme = true) {
        AuthScreen(vm)
    }
}
