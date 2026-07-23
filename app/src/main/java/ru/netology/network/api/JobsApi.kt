    package ru.netology.network.api

    import retrofit2.http.Body
    import retrofit2.http.DELETE
    import retrofit2.http.GET
    import retrofit2.http.POST
    import retrofit2.http.Path
    import ru.netology.network.dto.response.JobDto

    interface JobsApi {

        /** GET /api/{userId}/jobs - Работы любого пользователя */
        @GET("/api/{userId}/jobs")
        suspend fun getJobsByUser(@Path("userId") userId: Long): List<JobDto>

        /** POST /api/my/jobs - Создать свою работу */
        @POST("/api/my/jobs")
        suspend fun createJob(@Body job: JobDto): JobDto

        /** DELETE /api/my/jobs/{id} - Удалить свою работу */
        @DELETE("/api/my/jobs/{id}")
        suspend fun deleteJob(@Path("id") id: Long): Unit
    }