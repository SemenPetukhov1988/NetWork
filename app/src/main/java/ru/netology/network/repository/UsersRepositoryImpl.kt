package ru.netology.network.repository

import ru.netology.network.api.UsersApi
import ru.netology.network.dto.response.UserDto

class UsersRepositoryImpl(private val api: UsersApi) : UsersRepository {

    override suspend fun getAllUsers(): List<UserDto> {
        return api.getAllUsers()
    }

    override suspend fun getUserById(id: Long): UserDto {
        return api.getUserById(id)
    }
}
