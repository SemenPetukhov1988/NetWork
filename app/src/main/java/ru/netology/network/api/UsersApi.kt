    package ru.netology.network.api

    import okhttp3.MultipartBody
    import retrofit2.http.Body
    import retrofit2.http.GET
    import retrofit2.http.Multipart
    import retrofit2.http.POST
    import retrofit2.http.Part
    import retrofit2.http.Path
    import retrofit2.http.Query

    import ru.netology.network.dto.response.TokenDto

    import ru.netology.network.dto.response.UserDto


    interface UsersApi {
        @GET("api/users") // убрали начальный слэш
        suspend fun getAllUsers(): List<UserDto>

        @GET("api/users/{id}") // убрали начальный слэш
        suspend fun getUserById(@Path("id") id: Long): UserDto



        @POST("/api/users/registration")
        @Multipart // Обязательно! Без этого не сработает для Form Data
        suspend fun register(
            @Part("login") login: String,
            @Part("name") name: String,
            @Part("pass") pass: String,
            @Part avatar: MultipartBody.Part?
        ): TokenDto


        @POST("/api/users/authentication")
        @Multipart
        suspend fun login(
            @Part("login") login: String,
            @Part("pass") pass: String
        ): TokenDto
    }