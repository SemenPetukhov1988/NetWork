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
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository // Репозиторий теперь здесь!
) : ViewModel() {

    // Private изменяемый стейт
    private val _state = MutableStateFlow(AuthUiState())

    // Public неизменяемый стейт для Fragment
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)

                // Вызываем наш готовый репозиторий
                repository.login(login, pass)

                // Если выше не упало с Exception - значит успех
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Неизвестная ошибка"
                )
            }
        }
    }
}