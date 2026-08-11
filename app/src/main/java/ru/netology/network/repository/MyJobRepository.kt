package ru.netology.network.repository

import ru.netology.network.dto.response.JobResponse

interface MyJobRepository {
    suspend fun createJob(
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?
    ): JobResponse

    suspend fun getAllJobs(): List<JobResponse>
}