package ru.netology.network.dto.response

data class RegistrationRequest(
    val login: String,
    val password: String,
    val name: String
)

data class AuthenticationRequest(
    val login: String,
    val password: String
)

// --- ОТВЕТЫ (Response DTO) ---

data class RegistrationResponse(
    val id: Long,
    val token: String,
    val avatar: String? // Судя по примеру, может быть null или отсутствовать
)

data class AuthenticationResponse(
    val id: Long,
    val token: String,
    val avatar: String?
)

data class UserDto(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?,
    val about: String?
)
