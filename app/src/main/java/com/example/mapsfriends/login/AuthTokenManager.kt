package com.example.mapsfriends.login

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Suppress("DEPRECATION")
class AuthTokenManager @Inject constructor(@ApplicationContext context: Context) {
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
            .commit()
    }

    fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)
    fun getUserId(): String? = sharedPreferences.getString("user_id", null)

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}