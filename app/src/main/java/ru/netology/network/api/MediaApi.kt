package ru.netology.network.api

import retrofit2.http.Body
import retrofit2.http.POST
import ru.netology.network.dto.response.MediaUploadRequest

interface MediaApi {

    /**
     * POST /api/media
     * Загрузить медиафайл.
     *

     * Для загрузки файлов (картинок, видео) обычно используют @Multipart.

     * Если ты планируешь загружать реальные файлы с телефона, скажи — я покажу вариант с @Multipart.
     */
    @POST("/api/media")
    suspend fun uploadMedia(@Body request: MediaUploadRequest): MediaUploadRequest
}
