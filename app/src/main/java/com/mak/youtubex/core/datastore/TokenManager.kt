package com.mak.youtubex.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


interface JwtTokenManager {
    suspend fun saveUserDetails(id: String, name: String, email: String, avatar: String)
    suspend fun saveAccessJwt(accessToken: String)
    suspend fun saveRefreshJwt(refreshToken: String)
    fun getAccessJwt(): Flow<String?>
    fun getRefreshJwt(): Flow<String?>
    fun getUserId(): Flow<String?>
    suspend fun clearAllTokens()
}

@Singleton
class TokenManager @Inject constructor(
    private val authDataStore: DataStore<Preferences>,
    private val encryptor: Encryptor
): JwtTokenManager {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
    }

    override fun getUserId(): Flow<String?> {
        return authDataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }
    }

    override suspend fun saveUserDetails(
        id: String,
        name: String,
        email: String,
        avatar: String
    ) {
        authDataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            preferences[USER_AVATAR_KEY] = avatar
        }
    }

    override suspend fun saveAccessJwt(accessToken: String) {
        val encrypted = encryptor.encrypt(accessToken)
        authDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = encrypted
        }
    }
    override suspend fun saveRefreshJwt(refreshToken: String) {
        val encrypted = encryptor.encrypt(refreshToken)
        authDataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = encrypted
        }
    }

    override fun getAccessJwt(): Flow<String?> {
        return authDataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]?.let {
                encryptor.decrypt(it)
            }
        }
    }
    override fun getRefreshJwt(): Flow<String?> {
        return authDataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]?.let {
                encryptor.decrypt(it)
            }
        }
    }

    override suspend fun clearAllTokens() {
        authDataStore.edit { preferences->
            preferences.clear()
        }
    }
}
