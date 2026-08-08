package ru.netology.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.network.dto.response.TokenDto
import ru.netology.network.dto.statemodel.AuthUiState
import ru.netology.network.repository.AuthRepository
import ru.netology.network.repository.LocalAuthRepository

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val localAuthRepository: LocalAuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)

                // 1. Стучимся на сервер и получаем ответ (TokenDto)
                val response: TokenDto = repository.login(login, pass)

                android.util.Log.d("AUTH_DEBUG", "LOGIN SUCCESS HTTP")
                android.util.Log.d("AUTH_DEBUG", "Объект TokenDto целиком: $response")
                android.util.Log.d("AUTH_DEBUG", "Поле token из DTO: '${response.token}'")

                // 2. ВАЖНО: Сохраняем токен в локальное хранилище
                // Проверь, что в TokenDto поле называется именно token.
                // Если оно называется accessToken — поменяй response.token на response.accessToken
                localAuthRepository.saveToken(response.token)

                // 3. Помечаем успех
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)

            } catch (e: Exception) {
                // Ошибка сети, неверный пароль и т.д.
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Неизвестная ошибка"
                )
            }
        }
    }
    fun resetState() {
        _state.value = AuthUiState() // Сбрасываем всё в начальное состояние
    }
    // 4. Метод для кнопки «Выйти»
    fun logout() {
        viewModelScope.launch {
            // Удаляем токен из хранилища
            localAuthRepository.clearToken()

            // Сбрасываем состояние (если нужно)
            _state.value = AuthUiState()
        }
    }

    // 5. Метод, чтобы MainActivity мог проверить статус (автовход)
    suspend fun checkLoginStatus(): Boolean {
        return localAuthRepository.isLoggedIn()
    }
}