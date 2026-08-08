package ru.netology.network.dto.statemodel

import ru.netology.network.dto.response.PostDto

data class PostUiState(
    val isLoading: Boolean = false,
    val post: PostDto? = null,
    val errorMessage: String? = null
)
