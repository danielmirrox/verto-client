package com.samsung.verto.presentation.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.verto.R
import com.samsung.verto.data.local.SessionStore
import com.samsung.verto.domain.usecase.UseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val useCases: UseCases,
    private val sessionStore: SessionStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(isAuthenticated = sessionStore.accessToken != null)
    }

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun updateDisplayName(value: String) {
        _uiState.value = _uiState.value.copy(displayName = value)
    }

    fun updateToken(value: String) {
        _uiState.value = _uiState.value.copy(firebaseToken = value)
    }

    fun login() {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, errorResId = null)
        viewModelScope.launch {
            runCatching {
                useCases.auth(
                    firebaseToken = _uiState.value.firebaseToken.trim(),
                    email = _uiState.value.email.trim(),
                    displayName = _uiState.value.displayName.trim().ifEmpty { null },
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    isLoading = false,
                    error = null,
                    errorResId = null,
                )
            }.onFailure { error ->
                val message = error.message
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = message,
                    errorResId = if (message == null) R.string.error_auth_failed else null,
                )
            }
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val displayName: String = "",
    val firebaseToken: String = "dev-token",
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    @field:StringRes val errorResId: Int? = null,
)
