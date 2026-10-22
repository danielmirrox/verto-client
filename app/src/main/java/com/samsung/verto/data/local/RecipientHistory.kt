package com.samsung.verto.data.local
import com.samsung.verto.domain.model.User

interface RecipientHistory { fun load(): List<User>; fun save(users: List<User>) }
class MemoryRecipientHistory : RecipientHistory {
    private var users = emptyList<User>()
    override fun load() = users
    override fun save(users: List<User>) { this.users = users.take(10) }
}
