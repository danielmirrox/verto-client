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

class AuthViewModel(private val useCases: UseCases, private val sessionStore: SessionStore) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(isAuthenticated = sessionStore.accessToken != null))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    fun updateEmail(value: String) { _uiState.value = _uiState.value.copy(email = value) }
    fun updateDisplayName(value: String) { _uiState.value = _uiState.value.copy(displayName = value) }
    fun updatePassword(value: String) { _uiState.value = _uiState.value.copy(password = value) }
    fun toggleRegistration() { _uiState.value = _uiState.value.copy(isRegistering = !_uiState.value.isRegistering, error = null) }
    fun logout() { sessionStore.accessToken = null; _uiState.value = AuthUiState() }
    fun login() {
        if (_uiState.value.isLoading) return
        val form = _uiState.value
        _uiState.value = form.copy(isLoading = true, error = null, errorResId = null)
        viewModelScope.launch {
            runCatching { useCases.auth(form.password, form.email.trim(), form.displayName.trim().ifEmpty { null }, form.isRegistering) }
                .onSuccess { _uiState.value = _uiState.value.copy(isAuthenticated = true, isLoading = false, password = "") }
                .onFailure { error -> _uiState.value = _uiState.value.copy(isLoading = false, error = error.message, errorResId = if (error.message == null) R.string.error_auth_failed else null) }
        }
    }
}
data class AuthUiState(
    val email: String = "", val displayName: String = "", val password: String = "",
    val isRegistering: Boolean = false, val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false, val error: String? = null,
    @field:StringRes val errorResId: Int? = null,
)
