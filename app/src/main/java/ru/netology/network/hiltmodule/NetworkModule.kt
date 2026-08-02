    package ru.netology.network.hiltmodule
    import android.util.Log
    import dagger.Module
    import dagger.Provides
    import dagger.hilt.InstallIn
    import dagger.hilt.components.SingletonComponent
    import okhttp3.OkHttpClient
    import okhttp3.logging.HttpLoggingInterceptor
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import ru.netology.network.BuildConfig
    import ru.netology.network.api.EventsApi
    import ru.netology.network.api.GeneralWallApi
    import ru.netology.network.api.GlobalWallApi
    import ru.netology.network.api.JobsApi
    import ru.netology.network.api.MediaApi
    import ru.netology.network.api.MyWallApi
    import ru.netology.network.api.UsersApi

    import javax.inject.Singleton

    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkModule {

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                // ВАЖНО: этот интерцептор должен быть ПОСЛЕ логгера, но ДО всех остальных
                .addInterceptor { chain ->
                    var request = chain.request()

                    // Добавляем Api-Key явно и принудительно
                    request = request.newBuilder()
                        .addHeader("Api-Key", BuildConfig.API_KEY)
                        .build()
                    Log.d("NETWORK_DEBUG", "Отправляем запрос на: ${request.url}")
                    Log.d("NETWORK_DEBUG", "Заголовки запроса: ${request.headers}")
                    chain.proceed(request)
                }
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(client: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)

                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        fun provideGeneralWallApi(retrofit: Retrofit): GeneralWallApi {
            return retrofit.create(GeneralWallApi::class.java)
        }

        @Provides
        @Singleton
        fun provideMyWallApi(retrofit: Retrofit): MyWallApi {
            return retrofit.create(MyWallApi::class.java)
        }

        @Provides
        @Singleton
        fun provideJobsApi(retrofit: Retrofit): JobsApi {
            return retrofit.create(JobsApi::class.java)
        }

        @Provides
        @Singleton
        fun provideEventsApi(retrofit: Retrofit): EventsApi {
            return retrofit.create(EventsApi::class.java)
        }

        @Provides
        @Singleton
        fun provideMediaApi(retrofit: Retrofit): MediaApi {
            return retrofit.create(MediaApi::class.java)
        }

        @Provides
        @Singleton
        fun provideUsersApi(retrofit: Retrofit): UsersApi {
            return retrofit.create(UsersApi::class.java)
        }

        @Provides
        @Singleton
        fun provideGlobalWallApi(retrofit: Retrofit): GlobalWallApi {
            return retrofit.create(GlobalWallApi::class.java)
        }
    }