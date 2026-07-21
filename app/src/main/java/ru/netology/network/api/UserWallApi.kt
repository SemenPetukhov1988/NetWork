    package ru.netology.nework.api

    import retrofit2.http.DELETE
    import retrofit2.http.GET
    import retrofit2.http.POST
    import retrofit2.http.Path
    import retrofit2.http.Query
    import ru.netology.network.dto.response.PostDto


    interface UserWallApi {

        // --- ОСНОВНЫЕ ЗАПРОСЫ (как на картинке) ---

        // Получить ленту постов конкретного автора
        @GET("/api/{authorId}/wall")
        suspend fun getWall(
            @Path("authorId") authorId: Long,
            @Query("page") page: Int = 0,
            @Query("size") size: Int = 20
        ): List<PostDto>

        // Получить один пост
        @GET("/api/{authorId}/wall/{id}")
        suspend fun getPost(
            @Path("authorId") authorId: Long,
            @Path("id") id: Long
        ): PostDto

        // --- ПАГИНАЦИЯ (подгрузка) ---

        // Посты ПОСЛЕ указанного (для обновления ленты)
        @GET("/api/{authorId}/wall/{id}/newer")
        suspend fun getNewer(
            @Path("authorId") authorId: Long,
            @Path("id") id: Long,
            @Query("limit") limit: Int = 10
        ): List<PostDto>

        // Посты ДО указанного (для прокрутки вниз)
        @GET("/api/{authorId}/wall/{id}/before")
        suspend fun getBefore(
            @Path("authorId") authorId: Long,
            @Path("id") id: Long,
            @Query("limit") limit: Int = 10
        ): List<PostDto>

        // Посты ПОСЛЕ (альтернатива, зависит от логики сервера)
        @GET("/api/{authorId}/wall/{id}/after")
        suspend fun getAfter(
            @Path("authorId") authorId: Long,
            @Path("id") id: Long,
            @Query("limit") limit: Int = 10
        ): List<PostDto>

        // Получить самые свежие посты (быстрый старт)
        @GET("/api/{authorId}/wall/latest")
        suspend fun getLatest(
            @Path("authorId") authorId: Long,
            @Query("count") count: Int = 10
        ): List<PostDto>

        // --- ЛАЙКИ (самое важное с картинки) ---

        // ПОСТАВИТЬ лайк (POST)
        @POST("/api/{authorId}/wall/{id}/likes")
        suspend fun likePost(
            @Path("authorId") authorId: Long,
            @Path("id") id: Long
        ) // Возвращает Unit (пустой ответ)

        // СНЯТЬ лайк (DELETE)
        @DELETE("/api/{authorId}/wall/{id}/likes")
        suspend fun unlikePost(
            @Path("authorId") authorId: Long,
            @Path("id") id: Long
        ) // Возвращает Unit
    }

