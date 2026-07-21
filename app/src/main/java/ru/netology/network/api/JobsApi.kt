package ru.netology.network.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.network.dto.response.JobDto

interface JobsApi {
    /**
     * GET /api/{userId}/jobs
     * Получить список работ конкретного пользователя.
     * Если передать свой ID -> получим свои работы.
     * Если передать ID друга -> получим его работы (если бэкенд разрешает).
     */
    @GET("/api/{userId}/jobs")
    suspend fun getJobs(@Path("userId") userId: Long): List<JobDto>

    /**
     * POST /api/my/jobs (или /api/{userId}/jobs, проверь Swagger)
     * Создать новую работу. Обычно это делается только для себя.
     */
    @POST("/api/my/jobs")
    suspend fun createJob(@Body job: JobDto): JobDto

    /**
     * DELETE /api/my/jobs/{id}
     * Удалить свою работу.
     */
    @DELETE("/api/my/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long): Unit
}

