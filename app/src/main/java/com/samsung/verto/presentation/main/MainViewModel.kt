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

class MainViewModel(
    private val useCases: UseCases,
    private val sessionStore: SessionStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
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
        _uiState.value = _uiState.value.copy(searchQuery = value)
    }

    fun updateTransactionFilter(value: String) {
        _uiState.value = _uiState.value.copy(transactionFilter = value)
    }

    fun topup() {
        val amount = _uiState.value.topupAmount.toLongOrNull() ?: return
        viewModelScope.launch {
            runCatching { useCases.topup(amount) }
                .onSuccess { refresh() }
                .onFailure { error ->
                    val message = error.message
                    _uiState.value = _uiState.value.copy(
                        error = message,
                        errorResId = if (message == null) R.string.error_request_failed else null,
                    )
                }
        }
    }

    fun transfer() {
        val toUserId = _uiState.value.transferUserId.toLongOrNull() ?: return
        val amount = _uiState.value.transferAmount.toLongOrNull() ?: return
        viewModelScope.launch {
            runCatching { useCases.transfer(toUserId, amount) }
                .onSuccess { refresh() }
                .onFailure { error ->
                    val message = error.message
                    _uiState.value = _uiState.value.copy(
                        error = message,
                        errorResId = if (message == null) R.string.error_request_failed else null,
                    )
                }
        }
    }

    fun search() {
        val query = _uiState.value.searchQuery.trim()
        if (query.length < 2) return
        viewModelScope.launch {
            runCatching { useCases.searchUsers(query) }
                .onSuccess { users ->
                    _uiState.value = _uiState.value.copy(searchResults = users)
                }
                .onFailure { error ->
                    val message = error.message
                    _uiState.value = _uiState.value.copy(
                        error = message,
                        errorResId = if (message == null) R.string.error_request_failed else null,
                    )
                }
        }
    }
}

data class MainUiState(
    val user: User? = null,
    val balance: Balance? = null,
    val transactions: List<Transaction> = emptyList(),
    val searchResults: List<User> = emptyList(),
    val topupAmount: String = "",
    val transferUserId: String = "",
    val transferAmount: String = "",
    val searchQuery: String = "",
    val transactionFilter: String = "ALL",
    val isLoading: Boolean = false,
    val error: String? = null,
    @field:StringRes val errorResId: Int? = null,
)
