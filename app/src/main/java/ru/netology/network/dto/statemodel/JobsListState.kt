package ru.netology.network.dto.statemodel

import ru.netology.network.dto.response.JobResponse

data class JobsListState(
    val jobs: List<JobResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
