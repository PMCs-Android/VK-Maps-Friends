package com.example.mapsfriends

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mapsfriends.login.AuthTokenManager
import com.vk.api.sdk.VK
import com.vk.id.VKID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenManager: AuthTokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        try {
            // Инициализация VK SDK
            VK.initialize(this)
            Log.d("MainActivity", "VK SDK успешно инициализирован")
            
            // Инициализация VK ID
            VKID.init(this)
            Log.d("MainActivity", "VK ID успешно инициализирован")
            
            val startDestination = if (tokenManager.getAccessTokenSafely() != null) "main" else "login"
            
            Log.d("MainActivity", "Запуск приложения с начальным экраном: $startDestination")
            
            setContent {
                App(startDestination)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка при инициализации VK SDK", e)
            // В случае ошибки всегда запускаем экран входа
            setContent {
                App("login")
            }
        }
    }
}
