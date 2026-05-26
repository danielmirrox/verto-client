package com.samsung.verto.data.api

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    val firebaseToken: String,
    val email: String? = null,
    val displayName: String? = null,
)

@Serializable
data class AuthResponseDto(
    val userId: Long,
    val accessToken: String,
    val expiresAt: String,
)

@Serializable
data class UserResponseDto(
    val id: Long,
    val email: String,
    val displayName: String? = null,
)

@Serializable
data class BalanceResponseDto(
    val userId: Long,
    val balance: Long,
    val currency: String,
)

@Serializable
data class TopupRequestDto(
    val amount: Long,
)

@Serializable
data class TransferRequestDto(
    val toUserId: Long,
    val amount: Long,
)

@Serializable
data class TransactionResponseDto(
    val id: Long,
    val fromUserId: Long? = null,
    val toUserId: Long? = null,
    val amount: Long,
    val type: String,
    val createdAt: String,
)

@Serializable
data class TransactionsResponseDto(
    val items: List<TransactionResponseDto>,
    val total: Int,
)

@Serializable
data class UsersResponseDto(
    val items: List<UserResponseDto>,
)

@Serializable
data class ErrorResponseDto(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null,
)
