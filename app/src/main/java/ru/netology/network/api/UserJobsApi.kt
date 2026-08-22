package ru.netology.network.api

import retrofit2.http.GET
import retrofit2.http.Path
import ru.netology.network.dto.response.JobResponse

interface UserJobsApi {

    // Точно как на скриншоте: /api/{userId}/jobs
    @GET("/api/{userId}/jobs")
    suspend fun getJobs(
        @Path("userId") userId: Long
    ): List<JobResponse>
}
