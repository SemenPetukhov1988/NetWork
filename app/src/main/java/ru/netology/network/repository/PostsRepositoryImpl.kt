package ru.netology.network.repository

import ru.netology.network.api.PostsApi
import ru.netology.network.dto.response.CoordsDto
import ru.netology.network.dto.response.CreatePostRequest
import ru.netology.network.dto.response.PostDto
import java.io.File

class PostsRepositoryImpl(
    private val api: PostsApi
) : PostsRepository {

    override suspend fun createPost(
        content: String,
        imageFile: File?,
        latitude: Double?,
        longitude: Double?,
        link: String?
    ): PostDto {
        val coords = if (latitude != null && longitude != null) {
            CoordsDto(lat = latitude, long = longitude)
        } else null

        // Формируем ОДИН объект запроса
        val request = CreatePostRequest(
            content = content,
            link = link,
            coords = coords
        )

        // ❌ БЫЛО: return api.createPost(listOf(request))  <-- ЭТО ВЫЗЫВАЛО 400
        // ✅ СТАЛО: передаём просто объект, без listOf
        return api.createPost(request)
    }
}
