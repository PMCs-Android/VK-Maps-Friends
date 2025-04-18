package com.example.mapsfriends

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vk.id.VKID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        VKID.init(this)

        val tokenManager = AuthTokenManager(this)
        val startDestination = if (tokenManager.getAccessToken() != null) "main" else "login"

        setContent {
            App(startDestination)
        }
    }
}
