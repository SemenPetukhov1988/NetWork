package ru.netology.network.repository

import ru.netology.network.dto.response.UserDto

interface UsersRepository {
    suspend fun getAllUsers(): List<UserDto>
    suspend fun getUserById(id: Long): UserDto
}
