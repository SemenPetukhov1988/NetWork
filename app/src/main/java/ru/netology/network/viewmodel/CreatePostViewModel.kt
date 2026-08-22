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
import ru.netology.network.dto.response.PostDto
import ru.netology.network.dto.statemodel.CreatePostUiState
import ru.netology.network.repository.PostsRepository
import java.io.File

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    @Named("auth") private val repository: PostsRepository
) : ViewModel() {

    // Используем новый тип состояния с поддержкой retry
    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun clearPostState() {
        _uiState.update { it.copy(
            post = null,
            isLoading = false,
            errorMessage = null,
            canRetry = true
        ) }
    }

    fun createPost(
        content: String,
        imageFile: File?,
        latitude: Double?,
        longitude: Double?,
        link: String?
    ) = viewModelScope.launch {
        // 1. Сразу показываем, что идёт загрузка, и запрещаем повторную отправку пока грузится
        _uiState.update { it.copy(
            isLoading = true,
            errorMessage = null,
            canRetry = false
        ) }

        try {
            var imageUrl: String? = null

            // 2. Если файл есть — загружаем картинку и получаем URL
            if (imageFile != null && imageFile.exists()) {
                imageUrl = repository.uploadImage(imageFile)
                // Опционально: удаляем временный файл после загрузки
                // imageFile.delete()
            }

            // 3. Создаём пост, передавая URL картинки
            val post = repository.createPost(
                content = content,
                imageUrl = imageUrl,
                latitude = latitude,
                longitude = longitude,
                link = link
            )

            // 4. Успех: сохраняем пост, убираем лоадер
            _uiState.update { it.copy(
                post = post,
                isLoading = false,
                errorMessage = null,
                canRetry = true
            ) }

        } catch (e: Exception) {
            // 5. Ошибка (таймаут, сеть, сервер и т.д.)
            // Важно: ставим canRetry = true, чтобы пользователь мог нажать «Повторить»
            _uiState.update { it.copy(
                isLoading = false,
                errorMessage = e.message ?: "Произошла ошибка при создании поста. Проверьте соединение.",
                canRetry = true
            ) }
        }
    }
}
