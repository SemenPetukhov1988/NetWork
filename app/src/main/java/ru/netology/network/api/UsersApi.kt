package ru.netology.network.api

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

import ru.netology.network.dto.response.UserDto


interface UsersApi {
    @GET("/api/users")
    suspend fun getAllUsers(): List<UserDto>

    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDto

    @POST("/api/users/registration")
    suspend fun register(
        @Query("login") login: String,
        @Query("pass") pass: String,
        @Query("name") name: String,
        @Part file: MultipartBody.Part? = null
    ): Unit // Или верни TokenDto, если нужно

    @POST("/api/users/authentication")
    suspend fun login(
        @Query("login") login: String,
        @Query("pass") pass: String
    ): Unit // Или верни TokenDto
}