package com.example.mapsfriends

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
            
            // Проверяем наличие токена
            val token = tokenManager.getAccessTokenSafely()
            val startDestination = if (token != null) "main" else "login"
            
            Log.d("MainActivity", "Запуск приложения с начальным экраном: $startDestination")
            
            setContent {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        App(startDestination)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка при инициализации VK SDK", e)
            // В случае ошибки всегда запускаем экран входа
            setContent {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        App("login")
                    }
                }
            }
        }
    }
}
