package ru.netology.network.dto.statemodel

import ru.netology.network.dto.response.UserDto

data class UsersUiState(
    val isLoading: Boolean = false,
    val users: List<UserDto> = emptyList(),
    val errorMessage: String? = null
)
