package ru.netology.network.repository

import ru.netology.network.dto.response.PostDto
import java.io.File

interface PostsRepository {
    suspend fun createPost(
        content: String,
        imageFile: File? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        link: String? = null
    ): PostDto
}