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
            api.login(login = login, pass = pass)
        } catch (e: HttpException) {
            val code = e.code()
            when (code) {
                401, 404 -> throw Exception("Неверный логин или пароль. Попробуйте ещё раз.")
                400 -> throw Exception("Ошибка валидации данных")
                else -> throw Exception("Произошла ошибка сервера: $code")
            }
        }
    }

    override suspend fun register(login: String, pass: String, name: String): TokenDto {
        return try {
            // Передаём параметры по отдельности, Retrofit сделает из них форму
            api.register(login = login, name = name, pass = pass)
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> throw Exception("Пользователь с таким логином уже существует!")
                400 -> throw Exception("Ошибка валидации данных")
                else -> throw Exception("Сервер вернул ошибку: ${e.code()}")
            }
        }
    }
}