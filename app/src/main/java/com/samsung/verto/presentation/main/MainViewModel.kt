package com.samsung.verto.presentation.main

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.verto.R
import com.samsung.verto.data.local.SessionStore
import com.samsung.verto.domain.model.Balance
import com.samsung.verto.domain.model.Transaction
import com.samsung.verto.domain.model.User
import com.samsung.verto.domain.usecase.UseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import com.samsung.verto.data.local.RecipientHistory
import com.samsung.verto.data.local.MemoryRecipientHistory

class MainViewModel(
    private val useCases: UseCases,
    private val sessionStore: SessionStore,
    private val history: RecipientHistory = MemoryRecipientHistory(),
) : ViewModel() {
    private var searchJob: Job? = null
    private val _uiState = MutableStateFlow(MainUiState(recipientHistory = history.load()))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun loadInitial() {
        if (sessionStore.accessToken == null) return
        refresh()
    }

    fun refresh() {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, errorResId = null)
        viewModelScope.launch {
            runCatching {
                val user = useCases.getUser()
                val balance = useCases.getBalance()
                val transactions = useCases.getTransactions(limit = 50, offset = 0)
                Triple(user, balance, transactions)
            }.onSuccess { (user, balance, transactions) ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = user,
                    balance = balance,
                    transactions = transactions,
                    error = null,
                    errorResId = null,
                )
            }.onFailure { error ->
                val message = error.message
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = message,
                    errorResId = if (message == null) R.string.error_load_failed else null,
                )
            }
        }
    }

    fun updateTopupAmount(value: String) {
        _uiState.value = _uiState.value.copy(topupAmount = value)
    }

    fun updateTransferUserId(value: String) {
        _uiState.value = _uiState.value.copy(transferUserId = value)
    }

    fun updateTransferAmount(value: String) {
        _uiState.value = _uiState.value.copy(transferAmount = value)
    }

    fun updateSearchQuery(value: String) {
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(searchQuery = value, searchCompleted = false, searchLoading = false, searchError = null, searchResults = emptyList())
    }

    fun updateTransactionFilter(value: String) {
        _uiState.value = _uiState.value.copy(transactionFilter = value)
    }

    fun topup() {
        if (_uiState.value.isSubmitting) return
        val amount = _uiState.value.topupAmount.toLongOrNull() ?: return
        _uiState.value = _uiState.value.copy(isSubmitting = true)
        viewModelScope.launch {
            runCatching { useCases.topup(amount) }
                .onSuccess { _uiState.value = _uiState.value.copy(isSubmitting = false); refresh() }
                .onFailure { error ->
                    val message = error.message
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = message,
                        errorResId = if (message == null) R.string.error_request_failed else null,
                    )
                }
        }
    }

    fun transfer() {
        if (_uiState.value.isSubmitting) return
        val toUserId = _uiState.value.transferUserId.toLongOrNull() ?: return
        val amount = _uiState.value.transferAmount.toLongOrNull() ?: return
        _uiState.value = _uiState.value.copy(isSubmitting = true)
        viewModelScope.launch {
            runCatching { useCases.transfer(toUserId, amount) }
                .onSuccess { _uiState.value = _uiState.value.copy(isSubmitting = false); refresh() }
                .onFailure { error ->
                    val message = error.message
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = message,
                        errorResId = if (message == null) R.string.error_request_failed else null,
                    )
                }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(searchQuery = "", searchResults = emptyList(), searchCompleted = false, searchLoading = false, searchError = null)
    }
    fun selectRecipient(user: User) {
        val selected = (listOf(user) + _uiState.value.recipientHistory.filter { it.id != user.id }).take(10)
        history.save(selected)
        _uiState.value = _uiState.value.copy(transferUserId = user.id.toString(), recipientHistory = selected)
    }
    fun clearHistory() {
        history.save(emptyList())
        _uiState.value = _uiState.value.copy(recipientHistory = emptyList())
    }
    fun reset() {
        searchJob?.cancel()
        clearHistory()
        _uiState.value = MainUiState()
    }
    fun search() {
        val query = _uiState.value.searchQuery.trim()
        if (query.length < 2) return
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(searchLoading = true, searchError = null, searchCompleted = false)
        searchJob = viewModelScope.launch {
            try {
                val users = useCases.searchUsers(query)
                _uiState.value = _uiState.value.copy(searchResults = users, searchLoading = false, searchCompleted = true)
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) {
                _uiState.value = _uiState.value.copy(searchLoading = false, searchCompleted = false, searchError = "Не удалось найти получателя. Проверьте соединение и повторите.")
            }
        }
    }

}

data class MainUiState(
    val user: User? = null,
    val balance: Balance? = null,
    val transactions: List<Transaction> = emptyList(),
    val searchResults: List<User> = emptyList(),
    val recipientHistory: List<User> = emptyList(),
    val searchLoading: Boolean = false,
    val searchCompleted: Boolean = false,
    val searchError: String? = null,
    val topupAmount: String = "",
    val transferUserId: String = "",
    val transferAmount: String = "",
    val searchQuery: String = "",
    val transactionFilter: String = "ALL",
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    @field:StringRes val errorResId: Int? = null,
)
