package ru.netology.network.dto.response

data class JobDto(
    val id: Long,
    val name: String,
    val position: String,
    val start: String,      // Оставляем String для простоты
    val finish: String,
    val link: String
)
