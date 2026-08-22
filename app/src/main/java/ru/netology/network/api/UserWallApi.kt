package ru.netology.network.api

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.network.dto.response.PostDto

interface UserWallApi {

    // --- ЗАГРУЗКА СТЕНЫ ПОЛЬЗОВАТЕЛЯ ---

    /** GET /api/{authorId}/wall - Основная лента пользователя */
    @GET("/api/{authorId}/wall")
    suspend fun getUserWall(
        @Path("authorId") authorId: Long
    ): List<PostDto>

    /** GET /api/{authorId}/wall/latest - Последние посты пользователя */
    @GET("/api/{authorId}/wall/latest")
    suspend fun getUserLatest(
        @Path("authorId") authorId: Long,
        @Query("count") count: Int
    ): List<PostDto>

    /** GET /api/{authorId}/wall/{id}/newer - Новые посты после ID */
    @GET("/api/{authorId}/wall/{id}/newer")
    suspend fun getUserNewer(
        @Path("authorId") authorId: Long,
        @Path("id") postId: Long
    ): List<PostDto>

    /** GET /api/{authorId}/wall/{id}/before - Старые посты до ID */
    @GET("/api/{authorId}/wall/{id}/before")
    suspend fun getUserBefore(
        @Path("authorId") authorId: Long,
        @Path("id") postId: Long,
        @Query("count") count: Int
    ): List<PostDto>

    /** GET /api/{authorId}/wall/{id}/after - Посты после ID */
    @GET("/api/{authorId}/wall/{id}/after")
    suspend fun getUserAfter(
        @Path("authorId") authorId: Long,
        @Path("id") postId: Long,
        @Query("count") count: Int
    ): List<PostDto>

    /** GET /api/{authorId}/wall/{id} - Получить пост пользователя по ID */
    @GET("/api/{authorId}/wall/{id}")
    suspend fun getUserPostById(
        @Path("authorId") authorId: Long,
        @Path("id") postId: Long
    ): PostDto

    // --- ЛАЙКИ ДЛЯ СТЕНЫ ПОЛЬЗОВАТЕЛЯ ---

    @POST("/api/{authorId}/wall/{id}/likes")
    suspend fun likeUserPost(
        @Path("authorId") authorId: Long,
        @Path("id") postId: Long
    ): PostDto

    @DELETE("/api/{authorId}/wall/{id}/likes")
    suspend fun unlikeUserPost(
        @Path("authorId") authorId: Long,
        @Path("id") postId: Long
    ): PostDto
}
