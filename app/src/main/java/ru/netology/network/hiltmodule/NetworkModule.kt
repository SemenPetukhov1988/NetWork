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
import ru.netology.network.api.*
import ru.netology.network.repository.LocalAuthRepository
import ru.netology.network.repository.PostsRepository
import ru.netology.network.repository.PostsRepositoryImpl
import ru.netology.network.repository.UsersRepository
import ru.netology.network.repository.UsersRepositoryImpl

import jakarta.inject.Named
import jakarta.inject.Singleton
import ru.netology.network.repository.MyJobRepository
import ru.netology.network.repository.MyJobRepositoryImpl
import ru.netology.network.repository.UserJobsRepository
import ru.netology.network.repository.UserJobsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    // --- ЕДИНЫЙ КЛИЕНТ ДЛЯ АВТОРИЗОВАННЫХ ЗАПРОСОВ
    // Он ставит И Api-Key, И Authorization (токен)
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        localAuthRepository: LocalAuthRepository
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // 1. Сначала добавляем наш кастомный интерцептор с заголовками
            .addInterceptor { chain ->
                var request = chain.request()
                val token = localAuthRepository.getTokenSync()

                // Логи для отладки (можно удалить в релизе)

                Log.d("TOKEN_CHECK", "TOKEN_LENGTH: \${token?.length ?: 0}")

                // Строим новый запрос
                request = request.newBuilder()
                    // ВАЖНО 1: Добавляем Api-Key всегда (это признак нашего приложения)
                    .addHeader("Api-Key", BuildConfig.API_KEY)

                    // ВАЖНО 2: Добавляем токен ТОЛЬКО если он есть
                    .apply {
                        if (!token.isNullOrBlank()) {
                            // Отправляем ГОЛЫЙ токен (без слова Bearer), как требует твой бэкенд
                            addHeader("Authorization", token)
                        }
                    }
                    .build()

                chain.proceed(request)
            }
            // 2. Потом добавляем логирование, чтобы видеть уже финальный запрос с обоими заголовками
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // --- КЛИЕНТ ДЛЯ ПУБЛИЧНЫХ ЗАПРОСОВ (только чтение ленты)
    // Тут нужен только Api-Key, токен не нужен
    @Provides
    @Singleton
    @Named("normal")
    fun provideNormalOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                var request = chain.request()
                // Для публичных запросов нужен только Api-Key
                request = request.newBuilder()
                    .addHeader("Api-Key", BuildConfig.API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    // --- Retrofit для авторизованных запросов (создание постов, лайки и т.д.)
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(
        @Named("auth") client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- Retrofit для обычных запросов (лента)
    @Provides
    @Singleton
    @Named("normal")
    fun provideNormalRetrofit(
        @Named("normal") client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ==========================================
    // API PROVIDERS
    // ==========================================

    // PostsApi (создание поста) должен использовать клиент "auth"
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthPostsApi(
        @Named("auth") retrofit: Retrofit
    ): PostsApi {
        return retrofit.create(PostsApi::class.java)
    }

    // Остальные API (лента, вакансии и т.д.) используют клиент "normal"
    @Provides
    @Singleton
    @Named("normal")
    fun provideGlobalWallApi(
        @Named("normal") retrofit: Retrofit
    ): GlobalWallApi = retrofit.create(GlobalWallApi::class.java)

    @Provides
    @Singleton
    @Named("normal")
    fun provideGeneralWallApi(
        @Named("normal") retrofit: Retrofit
    ): GeneralWallApi = retrofit.create(GeneralWallApi::class.java)

    @Provides
    @Singleton
    @Named("auth")
    fun provideMyWallApi(
        @Named("auth") retrofit: Retrofit
    ): MyWallApi = retrofit.create(MyWallApi::class.java)

    @Provides
    @Singleton
    @Named("auth")
    fun provideMyJobsApi(
        @Named("auth") retrofit: Retrofit
    ): MyJobsApi = retrofit.create(MyJobsApi::class.java)

    @Provides
    @Singleton
    @Named("normal")
    fun provideEventsApi(
        @Named("normal") retrofit: Retrofit
    ): EventsApi = retrofit.create(EventsApi::class.java)

    @Provides
    @Singleton
    @Named("normal")
    fun provideMediaApi(
        @Named("normal") retrofit: Retrofit
    ): MediaApi = retrofit.create(MediaApi::class.java)

    @Provides
    @Singleton
    @Named("normal")
    fun provideUsersApi(
        @Named("normal") retrofit: Retrofit
    ): UsersApi = retrofit.create(UsersApi::class.java)

    // ==========================================
    // REPOSITORIES
    // ==========================================

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthPostsRepository(
        @Named("auth") api: PostsApi
    ): PostsRepository {
        return PostsRepositoryImpl(api)
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideMyJobRepository(
        @Named("auth") api: MyJobsApi
    ): MyJobRepository {
        return MyJobRepositoryImpl(api)
    }


    @Provides
    @Singleton
    @Named("normal")
    fun provideUsersRepository(
        @Named("normal") api: UsersApi
    ): UsersRepository {
        return UsersRepositoryImpl(api)
    }

    @Provides
    @Singleton
    @Named("normal")
    fun provideUserWallApi(
        @Named("normal") retrofit: Retrofit
    ): UserWallApi = retrofit.create(UserWallApi::class.java)

    @Provides
    @Singleton
    @Named("normal")
    fun provideUserJobsApi(
        @Named("normal") retrofit: Retrofit): UserJobsApi =
        retrofit.create(UserJobsApi::class.java)

    @Provides
    @Singleton
    @Named("normal")
    fun provideUserJobsRepository(
        @Named("normal") api: UserJobsApi
    ): UserJobsRepository {
        return UserJobsRepositoryImpl(api)
    }
}
