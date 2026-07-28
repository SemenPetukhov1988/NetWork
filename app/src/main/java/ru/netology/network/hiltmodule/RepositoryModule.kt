package ru.netology.network.hiltmodule

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.netology.network.api.UsersApi
import ru.netology.network.repository.AuthRepository
import ru.netology.network.repository.AuthRepositoryImpl
import ru.netology.network.repository.LocalAuthRepository
import ru.netology.network.repository.LocalAuthRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // ОДИН правильный провайдер для AuthRepository (через UsersApi)
    @Provides
    @Singleton
    fun provideAuthRepository(api: UsersApi): AuthRepository =
        AuthRepositoryImpl(api)

    // ОДИН провайдер для LocalAuthRepository (через Context)
    @Provides
    @Singleton
    fun provideLocalAuthRepository(
        @ApplicationContext context: Context
    ): LocalAuthRepository = LocalAuthRepositoryImpl(context)
}
