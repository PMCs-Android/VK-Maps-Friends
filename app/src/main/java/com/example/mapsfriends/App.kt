package com.example.mapsfriends

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mapsfriends.ui.login.LoginScreen

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen {
                navController.navigate("profile/$it")
            }
        }
        composable("main") { MainScreen(navController) }
        composable("events") { EventCalendarScreen(navController) }
        composable("requests") { RequestsScreen(navController) }
        composable("create") { RequestDetailsScreen(navController) }
        composable("eventDetails") { EventDetailsScreen(navController) }
        composable("messenger") { MessengerScreen(navController) }
        composable("requestDetails") { RequestDetailsScreen(navController) }
        composable("map") {
            MapScreen(navController = navController)
        }
        composable(
            "profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            ProfileScreen(
                navController = navController,
                id = backStackEntry.arguments?.getString("userId") ?: ""
            )
        }
    }
}
