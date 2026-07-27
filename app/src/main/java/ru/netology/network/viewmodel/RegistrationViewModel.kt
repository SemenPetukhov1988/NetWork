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

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    // Передаем все 3 параметра, как в твоем репозитории
    fun register(login: String, pass: String, name: String) {
        viewModelScope.launch {
            // 1. Ставим статус "Загрузка"
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                token = null
            )

            try {
                // 2. Вызываем репозиторий.
                // Если сервер ответит 200 OK, мы получим TokenDto
                val response = repository.register(login, pass, name)

                // 3. Сохраняем токен! Это твоя цель на сегодня
                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    token = response.token // Берем токен из ответа
                )

            } catch (e: Exception) {
                // Сюда попадают ошибки, которые ты сам выбросил в репозитории
                // (например, "Пользователь уже существует")
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Неизвестная ошибка"
                )
            }
        }
    }
}