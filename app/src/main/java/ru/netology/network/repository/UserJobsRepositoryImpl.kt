package ru.netology.network.repository

import jakarta.inject.Inject
import ru.netology.network.api.UserJobsApi

import ru.netology.network.dto.response.JobResponse

class UserJobsRepositoryImpl @Inject constructor(
    private val api: UserJobsApi
) : UserJobsRepository {

    override suspend fun getJobs(userId: Long): List<JobResponse> {
        // Пробрасываем вызов в API. 
        // Если сервер вернет ошибку (4xx/5xx) — Retrofit выбросит исключение,
        // и оно улетит в ViewModel, где ты его поймаешь в try/catch (как в твоем примере).
        return api.getJobs(userId)
    }
}