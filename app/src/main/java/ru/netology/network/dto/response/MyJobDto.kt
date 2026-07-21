package ru.netology.network.dto.response

data class MyJobDto(
    val id: Long,
    val name: String,
    val position: String,
    val start: String,      // Пока оставим String, как в примере JSON
    val finish: String,     // Пока оставим String
    val link: String
)
