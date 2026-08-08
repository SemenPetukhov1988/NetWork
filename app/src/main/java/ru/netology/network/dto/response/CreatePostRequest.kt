package ru.netology.network.dto.response

import java.io.File

// Это НЕ data class для ответа сервера.
// Это просто контейнер, чтобы удобно передавать параметры в функцию.
data class CreatePostRequest(
    val content: String,
    val published: String? = null,
    val coords: CoordsDto? = null,
    val link: String? = null,
    val attachment: AttachmentDto? = null
)
