package com.samsung.verto.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.samsung.verto.data.local.SessionStore
import com.samsung.verto.domain.usecase.UseCases
import com.samsung.verto.presentation.auth.AuthViewModel
import com.samsung.verto.presentation.main.MainViewModel

class AppViewModelFactory(
    private val useCases: UseCases,
    private val sessionStore: SessionStore,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(useCases, sessionStore) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(useCases, sessionStore) as T
            }
            else -> throw IllegalArgumentException("Неизвестный класс ViewModel")
        }
    }
}
