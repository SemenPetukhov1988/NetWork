package ru.netology.network.repository

import ru.netology.network.dto.response.TokenDto

interface AuthRepository {
    suspend fun login(login: String, pass: String): TokenDto
    suspend fun register(login: String, pass: String, name: String): TokenDto
}