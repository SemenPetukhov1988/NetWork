package ru.netology.network.api

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.network.dto.response.PostDto

interface MyWallApi {

    // --- ЗАГРУЗКА МОЕЙ ЛЕНТЫ ---

    /** GET /api/my/wall - Основная лента (автоматически подтягивает ID текущего юзера) */
    @GET("/api/my/wall")
    suspend fun getMyWall(): List<PostDto>

    /** GET /api/my/wall/latest - Последние посты */
    @GET("/api/my/wall/latest")
    suspend fun getMyLatest(@Query("count") count: Int): List<PostDto>

    /** GET /api/my/wall/{id}/newer - Новые посты после ID */
    @GET("/api/my/wall/{id}/newer")
    suspend fun getMyNewer(@Path("id") postId: Long): List<PostDto>

    /** GET /api/my/wall/{id}/before - Старые посты до ID */
    @GET("/api/my/wall/{id}/before")
    suspend fun getMyBefore(@Path("id") postId: Long, @Query("count") count: Int): List<PostDto>

    /** GET /api/my/wall/{id}/after - Посты после ID */
    @GET("/api/my/wall/{id}/after")
    suspend fun getMyAfter(@Path("id") postId: Long, @Query("count") count: Int): List<PostDto>

    /** GET /api/my/wall/{id} - Получить свой пост */
    @GET("/api/my/wall/{id}")
    suspend fun getMyPostById(@Path("id") postId: Long): PostDto

    // --- ЛАЙКИ ДЛЯ МОЕЙ ЛЕНТЫ (Специфичные эндпоинты из Swagger) ---
    // Примечание: В спецификации есть отдельные пути /api/my/wall/{id}/likes.
    // Используй их, если логика лайков в "Моей ленте" отличается от обычной.

    @POST("/api/my/wall/{id}/likes")
    suspend fun likeMyPost(@Path("id") postId: Long): PostDto

    @DELETE("/api/my/wall/{id}/likes")
    suspend fun unlikeMyPost(@Path("id") postId: Long): PostDto
}