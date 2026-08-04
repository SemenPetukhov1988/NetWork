package ru.netology.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.netology.network.dto.statemodel.UsersUiState
import ru.netology.network.repository.UsersRepository


@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            // Сначала ставим загрузку
            _uiState.value = UsersUiState(isLoading = true)

            try {
                val users = repository.getAllUsers()
                // Если всё ок — список и загрузка выключена
                _uiState.value = UsersUiState(users = users.reversed(), isLoading = false)
            } catch (e: Exception) {
                // Если ошибка — сообщение и загрузка выключена
                _uiState.value = UsersUiState(errorMessage = e.message ?: "Ошибка загрузки", isLoading = false)
            }
        }
    }
}
