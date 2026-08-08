package ru.netology.network.api


import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart

import retrofit2.http.POST
import retrofit2.http.Part

import ru.netology.network.dto.response.CreatePostRequest
import ru.netology.network.dto.response.ImageUploadResponse
import ru.netology.network.dto.response.PostDto

interface PostsApi {

    @POST("/api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): PostDto

    @Multipart
    @POST("/api/media")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>
}