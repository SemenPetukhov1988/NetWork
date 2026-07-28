package ru.netology.network.repository

interface LocalAuthRepository {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun isLoggedIn(): Boolean
}