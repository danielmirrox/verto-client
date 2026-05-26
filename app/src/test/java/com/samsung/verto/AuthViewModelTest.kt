package com.samsung.verto

import com.samsung.verto.data.local.InMemorySessionStore
import com.samsung.verto.domain.usecase.UseCases
import com.samsung.verto.presentation.auth.AuthViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loginMarksAuthenticated() = runTest {
        val repository = FakePaymentRepository()
        val useCases = UseCases(repository)
        val session = InMemorySessionStore()
        val viewModel = AuthViewModel(useCases, session)

        viewModel.updateEmail("demo@example.com")
        viewModel.updateToken("dev-token")
        viewModel.login()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAuthenticated)
    }
}
