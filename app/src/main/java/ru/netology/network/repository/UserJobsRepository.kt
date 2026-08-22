package ru.netology.network.repository

import ru.netology.network.dto.response.JobResponse

interface UserJobsRepository {
    suspend fun getJobs(userId: Long): List<JobResponse>
}