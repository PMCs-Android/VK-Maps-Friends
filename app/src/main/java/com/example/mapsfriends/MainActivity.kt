package com.example.mapsfriends

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mapsfriends.ui.login.LoginScreen
import com.example.mapsfriends.ui.login.data.LoginScreenObject
import com.example.mapsfriends.ui.profile.UserProfileScreen
import com.example.mapsfriends.ui.profile.data.ProfileScreenDataObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent() {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = LoginScreenObject) {

                composable<LoginScreenObject> {
                    LoginScreen{ navData ->
                        navController.navigate(navData)
                    }
                }

                composable<ProfileScreenDataObject> { navEntry ->
                    val navData = navEntry.toRoute<ProfileScreenDataObject>()
                    UserProfileScreen(navData)
                }

            }
        }
    }
}