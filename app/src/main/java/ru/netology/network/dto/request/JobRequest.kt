package ru.netology.network.dto.request

data class JobRequest(
    val name: String,
    val position: String,
    val start: String,      // ISO строка, например "2026-08-11T17:07:48.780Z"
    val finish: String?,     // ISO строка
    val link: String?
)