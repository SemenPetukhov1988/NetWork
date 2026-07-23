package ru.netology.network.dto.response

data class TokenDto(
    val id: Long,
    val token: String,
    val avatar: String? = null
)