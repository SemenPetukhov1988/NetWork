package ru.netology.network.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.network.dto.response.CommentDto
import ru.netology.network.dto.response.PostDto

interface GeneralWallApi {

    @GET("/api/posts")
    suspend fun getPosts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): List<PostDto>

    // 2. Создать пост
    // Внимание: В общей ленте создание поста может быть запрещено или перенаправлено.
    // Но по Swagger этот метод есть, поэтому оставляем.
    @POST("/api/posts")
    suspend fun createPost(@Body post: PostDto): PostDto

    // 3. ЛАЙК: Поставить
    @POST("/api/posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Long)

    // 4. ЛАЙК: Убрать
    @DELETE("/api/posts/{id}/likes")
    suspend fun unlikePost(@Path("id") id: Long)

    // 5. Получить один пост
    @GET("/api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): PostDto

    // 6. Удалить пост
    // В общей ленте удаление может работать только если ты автор.
    @DELETE("/api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long)

    // 7. Подгрузка новых (Pull-to-refresh)
    @GET("/api/posts/{id}/newer")
    suspend fun getNewerPosts(@Path("id") id: Long, @Query("limit") limit: Int = 10): List<PostDto>

    // 8. Подгрузка старых (скролл вниз)
    @GET("/api/posts/{id}/before")
    suspend fun getOlderPosts(@Path("id") id: Long, @Query("limit") limit: Int = 10): List<PostDto>

    // 9. Подгрузка "после" (альтернативная логика пагинации)
    @GET("/api/posts/{id}/after")
    suspend fun getAfterPosts(@Path("id") id: Long, @Query("limit") limit: Int = 10): List<PostDto>

    // 10. Быстрый старт (свежие посты)
    @GET("/api/posts/latest")
    suspend fun getLatestPosts(@Query("count") count: Int = 10): List<PostDto>

    // --- КОММЕНТАРИИ (добавляем новые методы) ---

   // Получить список комментариев к посту

    @GET("/api/posts/{postId}/comments")
    suspend fun getComments(@Path("postId") postId: Long): List<CommentDto>

      //Добавить новый комментарий

    @POST("/api/posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: Long,
        @Body comment: CommentDto
    ): CommentDto


     //Поставить лайк комментарию

    @POST("/api/posts/{postId}/comments/{id}/likes")
    suspend fun likeComment(
        @Path("postId") postId: Long,
        @Path("id") commentId: Long
    ): Unit


     //Убрать лайк с комментария

    @DELETE("/api/posts/{postId}/comments/{id}/likes")
    suspend fun unlikeComment(
        @Path("postId") postId: Long,
        @Path("id") commentId: Long
    ): Unit


     // Удалить комментарий (доступно только автору или админу)

    @DELETE("/api/posts/{postId}/comments/{id}")
    suspend fun deleteComment(
        @Path("postId") postId: Long,
        @Path("id") commentId: Long
    ): Unit
}
