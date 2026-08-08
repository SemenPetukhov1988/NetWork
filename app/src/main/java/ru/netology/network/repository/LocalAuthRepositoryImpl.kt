package ru.netology.network.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalAuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalAuthRepository {

    companion object {
        private const val PREF_NAME = "app_auth_prefs"
        private const val KEY_TOKEN = "auth_token"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Синхронный метод — нужен для NetworkModule (Interceptor)
    override fun getTokenSync(): String? = prefs.getString(KEY_TOKEN, null)

    override suspend fun saveToken(token: String) {
        withContext(Dispatchers.IO) {
            prefs.edit().putString(KEY_TOKEN, token).apply()
        }
    }

    override suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            prefs.getString(KEY_TOKEN, null)
        }
    }

    override suspend fun clearToken() {
        withContext(Dispatchers.IO) {
            prefs.edit().remove(KEY_TOKEN).apply()
        }
    }

    // Синхронная проверка — быстро и без корутин
    override fun isLoggedIn(): Boolean {
        return getTokenSync() != null
    }
}
