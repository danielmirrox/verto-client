package com.samsung.verto

import com.samsung.verto.data.local.InMemorySessionStore
import com.samsung.verto.domain.model.Balance
import com.samsung.verto.domain.model.Transaction
import com.samsung.verto.domain.model.User
import com.samsung.verto.domain.usecase.UseCases
import com.samsung.verto.presentation.main.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refreshLoadsUserBalanceAndTransactions() = runTest {
        val repository = FakePaymentRepository().apply {
            user = User(1, "demo@example.com", "Demo")
            balance = Balance(userId = 1, amount = 1500, currency = "RUB")
            transactions = listOf(Transaction(1, null, 1, 100, "TOPUP", "now"))
        }
        val useCases = UseCases(repository)
        val session = InMemorySessionStore().apply { accessToken = "token" }
        val viewModel = MainViewModel(useCases, session)

        viewModel.refresh()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("demo@example.com", state.user?.email)
        assertEquals(1500L, state.balance?.amount)
        assertEquals(1, state.transactions.size)
    }
}
