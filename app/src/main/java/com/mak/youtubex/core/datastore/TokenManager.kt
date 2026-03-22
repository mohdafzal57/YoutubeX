package com.mak.youtubex.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class UserSession(
    val id: String?,
    val name: String?,
    val email: String?,
    val avatar: String?,
    val accessToken: String?,
    val refreshToken: String?
) {
    val isLoggedIn: Boolean
        get() = !accessToken.isNullOrEmpty()
}

interface JwtTokenManager {

    val session: Flow<UserSession>

    suspend fun updateUser(
        id: String,
        name: String,
        email: String,
        avatar: String
    )

    suspend fun updateTokens(
        accessToken: String,
        refreshToken: String
    )

    suspend fun clearSession()
}

@Singleton
class TokenManager @Inject constructor(
    private val authDataStore: DataStore<Preferences>,
    private val encryptor: Encryptor
) : JwtTokenManager {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
    }

    override val session: Flow<UserSession> =
        authDataStore.data.map { pref ->

            val access = pref[ACCESS_TOKEN_KEY]?.let { encryptor.decrypt(it) }
            val refresh = pref[REFRESH_TOKEN_KEY]?.let { encryptor.decrypt(it) }

            UserSession(
                id = pref[USER_ID_KEY],
                name = pref[USER_NAME_KEY],
                email = pref[USER_EMAIL_KEY],
                avatar = pref[USER_AVATAR_KEY],
                accessToken = access,
                refreshToken = refresh
            )
        }

    override suspend fun updateUser(
        id: String,
        name: String,
        email: String,
        avatar: String
    ) {
        authDataStore.edit { pref ->
            pref[USER_ID_KEY] = id
            pref[USER_NAME_KEY] = name
            pref[USER_EMAIL_KEY] = email
            pref[USER_AVATAR_KEY] = avatar
        }
    }

    override suspend fun updateTokens(
        accessToken: String,
        refreshToken: String
    ) {
        val encryptedAccess = encryptor.encrypt(accessToken)
        val encryptedRefresh = encryptor.encrypt(refreshToken)

        authDataStore.edit { pref ->
            pref[ACCESS_TOKEN_KEY] = encryptedAccess
            pref[REFRESH_TOKEN_KEY] = encryptedRefresh
        }
    }

    override suspend fun clearSession() {
        authDataStore.edit { it.clear() }
    }
}