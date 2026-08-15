package ru.netology.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.network.dto.statemodel.AuthUiState
import ru.netology.network.repository.AuthRepository
import java.io.File

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    // Передаем все 3 параметра, как в твоем репозитории
    fun register(login: String, pass: String, name: String, avatarFile: File? = null) {
        viewModelScope.launch {
            // 1. Ставим статус "Загрузка"
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                token = null,
                isSuccess = false
            )

            try {
                // 2. Вызываем репозиторий, передавая туда файл (или null)
                // Репозиторий сам знает, как превратить этот File в Multipart-запрос
                val response = repository.register(login, pass, name, avatarFile)

                // 3. Сохраняем токен из ответа
                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    token = response.token
                )

            } catch (e: Exception) {
                // Сюда попадают ошибки от репозитория (например, 403 — пользователь занят)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Произошла ошибка при регистрации"
                )
            }
        }
    }
}