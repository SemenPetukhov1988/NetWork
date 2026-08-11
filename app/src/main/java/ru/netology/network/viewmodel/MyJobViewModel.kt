package ru.netology.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.netology.network.dto.statemodel.JobsListState
import ru.netology.network.repository.MyJobRepository

@HiltViewModel
class MyJobViewModel @Inject constructor(
    @Named("auth") private val repository: MyJobRepository
) : ViewModel() {

    // Один стейт: тут и список, и загрузка, и ошибки, и успехи
    private val _uiState = MutableStateFlow(JobsListState())
    val uiState: StateFlow<JobsListState> = _uiState.asStateFlow()

    fun loadJobs() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        try {
            val jobs = repository.getAllJobs()
            _uiState.update {
                it.copy(
                    jobs = jobs,
                    isLoading = false,
                    errorMessage = null,
                    successMessage = null
                )
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Не удалось загрузить список работ"
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = errorMsg,
                    jobs = emptyList()
                )
            }
        }
    }

    fun createJob(
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        try {
            repository.createJob(name, position, start, finish, link)

            // После создания — обновляем список, чтобы новая работа появилась
            loadJobs()

            _uiState.update {
                it.copy(successMessage = "Работа успешно создана!", isLoading = false)
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Произошла ошибка при создании работы"
            _uiState.update {
                it.copy(errorMessage = errorMsg, isLoading = false)
            }
        }
    }
}
