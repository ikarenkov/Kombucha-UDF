package com.github.ikarenkov.sample.shikimori.impl.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.ikarenkov.sample.shikimori.impl.auth.data.AccessTokenResponse
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class AuthDataLocalStorage(
    private val context: Context
) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("authData")
        private val AUTH_DATA_KEY = stringPreferencesKey("authData")

        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
            prettyPrint = true
        }
    }

    suspend fun getAccessTokensResponse(): AccessTokenResponse? = context.dataStore.data.first().let { preferences ->
        preferences[AUTH_DATA_KEY]?.let {
            json.decodeFromString<AccessTokenResponse>(it)
        }
    }

    suspend fun saveAccessTokens(accessTokenResponse: AccessTokenResponse?) {
        context.dataStore.edit { preferences ->
            val string = accessTokenResponse?.let { json.encodeToString<AccessTokenResponse>(it) }
            if (string == null) {
                preferences -= AUTH_DATA_KEY
            } else {
                preferences[AUTH_DATA_KEY] = string
            }
        }
    }
}