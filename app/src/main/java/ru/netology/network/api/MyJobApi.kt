package ru.netology.network.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.network.dto.response.MyJobDto

interface MyJobApi {

    @GET("/api/my/jobs")
    suspend fun getMyJobs(): List<MyJobDto>

    /**
     * POST /api/my/jobs
     * Создать новую работу
     */
    @POST("/api/my/jobs")
    suspend fun createMyJob(@Body job: MyJobDto): MyJobDto

    /**
     * DELETE /api/my/jobs/{id}
     * Удалить работу по ID
     */
    @DELETE("/api/my/jobs/{id}")
    suspend fun deleteMyJob(@Path("id") id: Long): Unit
}
