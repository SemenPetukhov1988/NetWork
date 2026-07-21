package ru.netology.network.api

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.network.dto.response.PostDto

interface MyWallApi {
    // 1. Основная лента (для Paging 3)
    @GET("/api/my/wall")
    suspend fun getWall(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): List<PostDto>

    // 2. Самые свежие посты (для быстрого старта)
    @GET("/api/my/wall/latest")
    suspend fun getLatest(
        @Query("count") count: Int = 10
    ): List<PostDto>

    // 3. Один пост по ID
    @GET("/api/my/wall/{id}")
    suspend fun getPost(
        @Path("id") id: Long
    ): PostDto

    // 4. Подгрузка новых (Pull-to-refresh)
    @GET("/api/my/wall/{id}/newer")
    suspend fun getNewer(
        @Path("id") id: Long,
        @Query("limit") limit: Int = 10
    ): List<PostDto>

    // 5. Подгрузка старых (скролл вниз)
    @GET("/api/my/wall/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("limit") limit: Int = 10
    ): List<PostDto>

    // 6. ЛАЙК: ПОСТАВИТЬ (POST)
    @POST("/api/my/wall/{id}/likes")
    suspend fun likePost(
        @Path("id") id: Long
    ) // Возвращает Unit (200 OK)

    // 7. ЛАЙК: УБРАТЬ (DELETE)
    @DELETE("/api/my/wall/{id}/likes")
    suspend fun unlikePost(
        @Path("id") id: Long
    ) // Возвращает Unit (204 No Content)
}