package com.samsung.verto

import com.samsung.verto.data.api.ApiClient
import com.samsung.verto.data.local.InMemorySessionStore
import com.samsung.verto.data.repository.PaymentRepositoryImpl
import com.samsung.verto.domain.usecase.UseCases

class AppContainer {
    private val sessionStore = InMemorySessionStore()
    private val apiClient = ApiClient(sessionStore)
    private val repository = PaymentRepositoryImpl(apiClient, sessionStore)

    val useCases = UseCases(repository)
    val session = sessionStore
}
