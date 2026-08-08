package ru.netology.network.repository

import ru.netology.network.dto.response.PostDto
import java.io.File

interface PostsRepository {
    suspend fun uploadImage(file: File): String
    suspend fun createPost(
        content: String,
        imageUrl: String?,
        latitude: Double?,
        longitude: Double?,
        link: String?
    ): PostDto

}