package ru.netology.network.repository

interface LocalAuthRepository {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()

    // Важно: здесь НЕ ставим suspend — это синхронный метод для сети
    fun getTokenSync(): String?

    // И этот тоже синхронный — для быстрых проверок
    fun isLoggedIn(): Boolean

    suspend fun saveUserId(userId: Long)
    suspend fun getUserId(): Long?
    suspend fun clearUserId()
}