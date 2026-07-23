package ru.netology.network.dto.response

data class JobDto(
    val id: Long,
    val name: String,
    val position: String,
    val start: String, // date-time
    val finish: String? = null,
    val link: String? = null
)