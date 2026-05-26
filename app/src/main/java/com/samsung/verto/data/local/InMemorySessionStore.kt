package com.samsung.verto.data.local

class InMemorySessionStore : SessionStore {
    override var accessToken: String? = null
}
