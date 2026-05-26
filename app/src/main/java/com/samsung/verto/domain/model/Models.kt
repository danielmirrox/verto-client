package com.samsung.verto.domain.model

data class User(
    val id: Long,
    val email: String,
    val displayName: String? = null,
)

data class Balance(
    val userId: Long,
    val amount: Long,
    val currency: String,
)

data class Transaction(
    val id: Long,
    val fromUserId: Long? = null,
    val toUserId: Long? = null,
    val amount: Long,
    val type: String,
    val createdAt: String,
)

data class AuthResult(
    val user: User,
    val accessToken: String,
    val expiresAt: String,
)
