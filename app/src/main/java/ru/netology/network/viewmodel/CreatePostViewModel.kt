package ru.netology.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.network.dto.response.PostDto
import ru.netology.network.dto.statemodel.PostUiState
import ru.netology.network.repository.PostsRepository

import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.update
import java.io.File

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    @Named("auth") private val repository: PostsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    fun clearPostState() {
        _uiState.update { it.copy(post = null) }
    }
    fun createPost(
        content: String,
        imageFile: File?, // Получаем файл из фрагмента
        latitude: Double?,
        longitude: Double?,
        link: String?
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }

        try {
            var imageUrl: String? = null

            // 1. Если файл есть -> грузим его и получаем URL
            if (imageFile != null && imageFile.exists()) {
                imageUrl = repository.uploadImage(imageFile)
                // Можно удалить временный файл: imageFile.delete()
            }

            // 2. Создаем пост, передавая URL вместо файла
            val post = repository.createPost(
                content = content,
                imageUrl = imageUrl, // Передаем ссылку
                latitude = latitude,
                longitude = longitude,
                link = link
            )

            _uiState.update { it.copy(post = post, isLoading = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.message ?: "Ошибка", isLoading = false) }
        }
    }
}
