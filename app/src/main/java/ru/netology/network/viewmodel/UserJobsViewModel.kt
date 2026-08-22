package ru.netology.network.viewmodel

import ru.netology.network.repository.UserJobsRepository
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


@HiltViewModel
class UserJobsViewModel @Inject constructor(
    @Named("normal") private val repository: UserJobsRepository
) : ViewModel() {

    // Один стейт: тут и список, и загрузка, и ошибки, и успехи
    private val _uiState = MutableStateFlow(JobsListState())
    val uiState: StateFlow<JobsListState> = _uiState.asStateFlow()

    /**
     * Загружает список работ для конкретного пользователя.
     * Вызывается из фрагмента, куда передаётся authorId.
     */
    fun loadJobs(userId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        try {
            val jobs = repository.getJobs(userId)
            _uiState.update {
                it.copy(
                    jobs = jobs,
                    isLoading = false,
                    errorMessage = null,
                    successMessage = null
                )
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Не удалось загрузить список работ пользователя"
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = errorMsg,
                    jobs = emptyList()
                )
            }
        }
    }
}
