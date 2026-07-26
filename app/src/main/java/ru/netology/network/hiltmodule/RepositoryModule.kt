package ru.netology.network.hiltmodule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.netology.network.api.UsersApi
import ru.netology.network.repository.AuthRepository
import ru.netology.network.repository.AuthRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository {
        return impl
    }

    // Если ты хочешь инжектить именно реализацию напрямую, можно так:
    @Provides
    @Singleton
    fun provideAuthRepositoryImpl(api: UsersApi): AuthRepositoryImpl {
        return AuthRepositoryImpl(api)
    }
}