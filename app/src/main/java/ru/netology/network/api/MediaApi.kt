package ru.netology.network.api

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.netology.network.dto.response.MediaDto


interface MediaApi {
    @Multipart
    @POST("/api/media")
    suspend fun uploadMedia(@Part file: MultipartBody.Part): MediaDto
}