package ru.netology.network.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

import ru.netology.network.dto.response.PostDto

interface GeneralWallApi {

    // --- ЗАГРУЗКА ЛЕНТЫ (ЛЮБОГО ПОЛЬЗОВАТЕЛЯ) ---

    /** GET /api/{authorId}/wall - Лента постов конкретного автора */
    @GET("/api/{authorId}/wall")
    suspend fun getWallByAuthor(@Path("authorId") authorId: Long): List<PostDto>

    /** GET /api/{authorId}/wall/latest - Последние посты автора (с пагинацией count) */
    @GET("/api/{authorId}/wall/latest")
    suspend fun getLatestWallByAuthor(@Path("authorId") authorId: Long, @Query("count") count: Int): List<PostDto>

    /** GET /api/{authorId}/wall/{id}/newer - Посты, созданные ПОСЛЕ указанного ID */
    @GET("/api/{authorId}/wall/{id}/newer")
    suspend fun getNewerWallByAuthor(@Path("authorId") authorId: Long, @Path("id") postId: Long): List<PostDto>

    /** GET /api/{authorId}/wall/{id}/before - Посты, созданные ДО указанного ID (с count) */
    @GET("/api/{authorId}/wall/{id}/before")
    suspend fun getBeforeWallByAuthor(@Path("authorId") authorId: Long, @Path("id") postId: Long, @Query("count") count: Int): List<PostDto>

    /** GET /api/{authorId}/wall/{id}/after - Посты, созданные ПОСЛЕ (аналог newer, зависит от логики бэкенда) */
    @GET("/api/{authorId}/wall/{id}/after")
    suspend fun getAfterWallByAuthor(@Path("authorId") authorId: Long, @Path("id") postId: Long, @Query("count") count: Int): List<PostDto>

    /** GET /api/{authorId}/wall/{id} - Получить один пост по ID */
    @GET("/api/{authorId}/wall/{id}")
    suspend fun getPostById(@Path("authorId") authorId: Long, @Path("id") postId: Long): PostDto

    // --- СОЗДАНИЕ И УДАЛЕНИЕ (Только для себя, поэтому пути /api/posts) ---

    /** POST /api/posts - Создать пост */
    @POST("/api/posts")
    suspend fun createPost(@Body post: PostDto): PostDto

    /** DELETE /api/posts/{id} - Удалить свой пост */
    @DELETE("/api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Unit

    // --- ВЗАИМОДЕЙСТВИЯ (Лайки) ---

    /** POST /api/{authorId}/wall/{id}/likes - Поставить лайк */
    @POST("/api/{authorId}/wall/{id}/likes")
    suspend fun likePost(@Path("authorId") authorId: Long, @Path("id") postId: Long): PostDto

    /** DELETE /api/{authorId}/wall/{id}/likes - Убрать лайк */
    @DELETE("/api/{authorId}/wall/{id}/likes")
    suspend fun unlikePost(@Path("authorId") authorId: Long, @Path("id") postId: Long): PostDto
}
