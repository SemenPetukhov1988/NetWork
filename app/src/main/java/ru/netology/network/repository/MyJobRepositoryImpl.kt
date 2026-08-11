package ru.netology.network.repository

import ru.netology.network.api.MyJobsApi
import ru.netology.network.dto.request.JobRequest
import ru.netology.network.dto.response.JobResponse

class MyJobRepositoryImpl(
    private val api: MyJobsApi
) : MyJobRepository {

    override suspend fun createJob(
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?
    ): JobResponse { // Возвращаем сразу DTO, как PostDto в createPost

        // 1. Собираем запрос (точно как CreatePostRequest)
        val request = JobRequest(
            name = name,
            position = position,
            start = start,
            finish = finish,
            link = link
        )

        // 2. Кидаем в API и сразу возвращаем результат
        // Если сервер вернёт 404 или 403 — Retrofit выбросит исключение здесь,
        // и оно улетит в ViewModel, где ты его поймаешь в try/catch.
        return api.createJob(request)
    }
    override suspend fun getAllJobs(): List<JobResponse> {
        return api.getJobs()
    }
}
