package ru.netology.network.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.network.dto.response.AuthenticationRequest
import ru.netology.network.dto.response.AuthenticationResponse
import ru.netology.network.dto.response.RegistrationRequest
import ru.netology.network.dto.response.RegistrationResponse
import ru.netology.network.dto.response.UserProfileDto

interface UsersApi {
    @POST("/api/users/registration")
    suspend fun register(@Body request: RegistrationRequest): RegistrationResponse

    @POST("/api/users/authentication")
    suspend fun authenticate(@Body request: AuthenticationRequest): AuthenticationResponse

    @GET("/api/users")
    suspend fun getAllUsers(): List<UserProfileDto> // <-- Используем новое имя

    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserProfileDto // <-- Используем новое имя
}