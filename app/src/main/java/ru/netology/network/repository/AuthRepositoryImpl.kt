package ru.netology.network.repository

import retrofit2.HttpException
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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
                401, 404 -> throw Exception("Юзер не зарегестрирован.Попробуйте ещё раз.")
                400 -> throw Exception("Неправильный пароль")
                else -> throw Exception("Произошла ошибка сервера: $code")
            }
        }
    }

    override suspend fun register(
        login: String,
        pass: String,
        name: String,
        avatarFile: java.io.File? // <-- Принимает сам файл или null
    ): TokenDto {
        return try {
            // Если файл есть — делаем из него MultipartBody.Part
            val avatarPart = if (avatarFile != null) {
                val requestBody = avatarFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", avatarFile.name, requestBody)
            } else {
                null
            }

            // Отправляем в API
            api.register(
                login = login,
                name = name,
                pass = pass,
                avatar = avatarPart
            )
        } catch (e: HttpException) {
            when (e.code()) {
                403 -> throw Exception("Пользователь с таким логином уже существует!")
                400 -> throw Exception("Ошибка валидации данных")
                else -> throw Exception("Сервер вернул ошибку: ${e.code()}")
            }
        }
    }
}
