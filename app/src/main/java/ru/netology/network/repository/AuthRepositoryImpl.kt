package ru.netology.network.repository

import retrofit2.HttpException
import jakarta.inject.Inject
import ru.netology.network.api.UsersApi
import ru.netology.network.dto.response.TokenDto

class AuthRepositoryImpl @Inject constructor(
    private val api: UsersApi
) : AuthRepository {

    override suspend fun login(login: String, pass: String): TokenDto {
        return try {
            // Вот здесь точка отказа. Убедись, что "return" стоит ПЕРЕД try
            api.login(login = login, pass = pass)
        } catch (e: HttpException) {
            throw Exception("Ошибка сети: ${e.code()}")
        }
    }

    override suspend fun register(login: String, pass: String, name: String): TokenDto {
        return try {
            api.register(login = login, pass = pass, name = name)
        } catch (e: HttpException) {
            if (e.code() == 403) {
                throw Exception("Пользователь уже существует")
            } else {
                throw Exception("Ошибка сервера")
            }
        }
    }
}