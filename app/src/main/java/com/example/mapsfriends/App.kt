package com.example.mapsfriends

<<<<<<< HEAD
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsfriends.ui.login.LoginScreen

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = "login",
    ) {
        composable("login") {
            LoginScreen { navData ->
                navController.navigate("main")
            }
        }
        composable("main") { MainScreen(navController) }
        composable("events") { EventCalendarScreen(navController) }
        composable("requests") { RequestsScreen(navController) }
        composable("create") { RequestDetailsScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("eventDetails") { EventDetailsScreen(navController) }
        composable("messenger") { MessengerScreen(navController) }
        composable("requestDetails") { RequestDetailsScreen(navController) }
    }
}
=======
import android.app.Application
import com.vk.id.VKID

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        VKID.init(this)
    }
}
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
