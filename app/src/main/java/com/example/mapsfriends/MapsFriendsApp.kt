package com.example.mapsfriends

import android.app.Application
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import com.vk.api.sdk.VK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MapsFriendsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Инициализация Google Play Services
            ProviderInstaller.installIfNeeded(this)
            Log.d("MapsFriendsApp", "Google Play Services успешно инициализированы")
            
            // Инициализация VK SDK
            VK.initialize(this)
            Log.d("MapsFriendsApp", "VK SDK успешно инициализирован")
        } catch (e: Exception) {
            Log.e("MapsFriendsApp", "Ошибка при инициализации сервисов", e)
        }
    }
} 