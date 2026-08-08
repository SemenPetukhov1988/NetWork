package ru.netology.network.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.netology.network.api.PostsApi
import ru.netology.network.dto.response.AttachmentDto
import ru.netology.network.dto.response.CoordsDto
import ru.netology.network.dto.response.CreatePostRequest
import ru.netology.network.dto.response.ImageUploadResponse
import ru.netology.network.dto.response.PostDto
import java.io.File

class PostsRepositoryImpl(
    private val api: PostsApi
) : PostsRepository {

    /**
     * Загружает файл на сервер по эндпоинту /api/media.
     * Имя поля "file" взято строго из спецификации Swagger.
     */
    override suspend fun uploadImage(file: File): String {
        // Определяем тип контента. Для универсальности можно проверять расширение,
        // но image/jpeg обычно подходит для большинства случаев.
        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val requestBody = file.asRequestBody(mediaType)

        // ВАЖНО: Первый аргумент "file" должен совпадать с именем поля в Swagger!
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val response: Response<ImageUploadResponse> = api.uploadImage(part)

        if (!response.isSuccessful || response.body() == null) {
            val errorCode = response.code()
            val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка загрузки"
            throw IllegalStateException("Ошибка загрузки картинки (код $errorCode): $errorMessage")
        }

        return response.body()!!.url
    }

    /**
     * Создает пост.
     * Обратите внимание: теперь сюда передается imageUrl (String), а не File.
     * Файл должен быть загружен заранее через uploadImage.
     */
    override suspend fun createPost(
        content: String,
        imageUrl: String?,
        latitude: Double?,
        longitude: Double?,
        link: String?
    ): PostDto {
        // Если есть картинка — делаем из неё AttachmentDto, иначе null
        val attachment = if (!imageUrl.isNullOrEmpty()) {
            AttachmentDto(url = imageUrl, type = "IMAGE")
        } else {
            null
        }

        // Пока не используем координаты и ссылку — передаём их в запрос, если захочешь позже
        return api.createPost(
            CreatePostRequest(
                content = content,
                attachment = attachment
                // latitude, longitude, link можно добавить в CreatePostRequest, если сервер их ждёт
            )
        )
    }
}

