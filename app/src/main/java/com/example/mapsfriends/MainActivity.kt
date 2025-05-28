package com.example.mapsfriends

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mapsfriends.login.AuthTokenManager
import com.vk.id.VKID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        VKID.init(this)
        requestLocationPermissions()
        val tokenManager = AuthTokenManager(this)
        val startDestination = if (tokenManager.getAccessToken() != null) "main" else "login"

        setContent {
            App(startDestination)
        }
    }
    private fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissions.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        ) {
            ActivityCompat.requestPermissions(this, permissions, R.dimen.request_code)
        }
    }
}
