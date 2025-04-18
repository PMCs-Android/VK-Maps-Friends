package com.example.mapsfriends

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

@Suppress("DEPRECATION")
class AuthTokenManager(context: Context) {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthData(token: String, userId: String) {
        sharedPreferences.edit()
            .putString("access_token", token)
            .putString("user_id", userId)
            .apply()
    }

    fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)
    fun getUserId(): Long? = sharedPreferences.getLong("user_id", -1L).takeIf { it != -1L }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
