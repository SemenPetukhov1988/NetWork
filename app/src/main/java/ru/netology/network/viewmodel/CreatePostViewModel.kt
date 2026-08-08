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
        imageFile: java.io.File?,
        latitude: Double?,
        longitude: Double?,
        link: String?
    ) {
        _uiState.value = PostUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val newPost = repository.createPost(
                    content = content,
                    imageFile = imageFile,
                    latitude = latitude,
                    longitude = longitude,
                    link = link
                )
                _uiState.value = PostUiState(post = newPost)
            } catch (e: Exception) {
                _uiState.value = PostUiState(errorMessage = e.message ?: "Не удалось опубликовать пост")
            }
        }
    }
}
