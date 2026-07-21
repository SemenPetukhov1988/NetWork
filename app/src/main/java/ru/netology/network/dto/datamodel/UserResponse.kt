package ru.netology.network.dto.datamodel

data class UserResponse(
    val id: Long,
    val login: String,
    val name : String,
    val avatar : String
)