package ru.netology.network.dto.statemodel

import ru.netology.network.dto.response.PostDto

data class CreatePostUiState(
    val isLoading: Boolean = false,
    val post: PostDto? = null,
    val errorMessage: String? = null,
    val canRetry: Boolean = true // <-- новое поле: можно ли нажать «Повторить»
)
