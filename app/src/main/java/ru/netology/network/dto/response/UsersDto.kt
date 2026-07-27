package ru.netology.network.dto.response



data class UserDto(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null
)

