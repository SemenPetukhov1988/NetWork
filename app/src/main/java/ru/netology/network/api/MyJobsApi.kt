    package ru.netology.network.api

    import retrofit2.Response
    import retrofit2.http.Body
    import retrofit2.http.GET
    import retrofit2.http.POST
    import ru.netology.network.dto.request.JobRequest
    import ru.netology.network.dto.response.JobResponse

    interface MyJobsApi {

        @POST("/api/my/jobs")
        suspend fun createJob(@Body request: JobRequest):JobResponse

        @GET("/api/my/jobs")
        suspend fun getJobs(): List<JobResponse>
    }