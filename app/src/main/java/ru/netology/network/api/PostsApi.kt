package ru.netology.network.api


import retrofit2.http.Body

import retrofit2.http.POST

import ru.netology.network.dto.response.CreatePostRequest
import ru.netology.network.dto.response.PostDto

interface PostsApi {

    @POST("/api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): PostDto
}