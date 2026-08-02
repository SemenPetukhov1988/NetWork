package ru.netology.network.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.network.dto.response.PostDto

interface GlobalWallApi {
    // Первые посты
    @GET("/api/posts/latest")
    suspend fun getLatestPosts(@Query("count") count: Int): List<PostDto>

    // Посты, которые были ДО указанного ID (для подгрузки вниз)
    @GET("/api/posts/{id}/before")
    suspend fun getPostsBefore(@Path("id") postId: Long, @Query("count") count: Int): List<PostDto>
}