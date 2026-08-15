package ru.netology.network.repository

import retrofit2.HttpException
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.network.api.UsersApi

import ru.netology.network.dto.response.TokenDto

class AuthRepositoryImpl @Inject constructor(
    private val api: UsersApi,
    private val localAuthRepository: LocalAuthRepository
) : AuthRepository {

    override suspend fun login(login: String, pass: String): TokenDto {
        return try {
            val response = api.login(login = login, pass = pass)

            // 1. Сохраняем токен
            localAuthRepository.saveToken(response.token)
            // 2. Сохраняем ID (обязательно!)
            localAuthRepository.saveUserId(response.id)

            return response
        } catch (e: HttpException) {
            // ... твой существующий код обработки ошибок ...
            val code = e.code()
            when (code) {
                401, 404 -> throw Exception("Юзер не зарегестрирован. Попробуйте ещё раз.")
                400 -> throw Exception("Неправильный пароль")
                else -> throw Exception("Произошла ошибка сервера: \$code")
            }
        }
    }

    override suspend fun register(
        login: String,
        pass: String,
        name: String,
        avatarFile: java.io.File?
    ): TokenDto {
        return try {
            val avatarPart = if (avatarFile != null) {
                val requestBody = avatarFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", avatarFile.name, requestBody)
            } else {
                null
            }

            val response = api.register(
                login = login,
                name = name,
                pass = pass,
                avatar = avatarPart
            )

            // 1. Сохраняем токен
            localAuthRepository.saveToken(response.token)
            // 2. Сохраняем ID
            localAuthRepository.saveUserId(response.id)

            return response
        } catch (e: HttpException) {
            // ... твой существующий код обработки ошибок ...
            when (e.code()) {
                403 -> throw Exception("Пользователь с таким логином уже существует!")
                400 -> throw Exception("Ошибка валидации данных")
                else -> throw Exception("Сервер вернул ошибку: \${e.code()}")
            }
        }
    }
}
