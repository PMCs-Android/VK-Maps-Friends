package com.example.mapsfriends

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
<<<<<<< HEAD
import com.vk.id.VKID
=======
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mapsfriends.ui.login.LoginScreen
import com.example.mapsfriends.ui.login.data.LoginScreenObject
import com.example.mapsfriends.ui.profile.UserProfileScreen
import com.example.mapsfriends.ui.profile.data.ProfileScreenDataObject
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
<<<<<<< HEAD
        VKID.init(this)
        setContent {
            App()
        }
    }
}
=======
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
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
