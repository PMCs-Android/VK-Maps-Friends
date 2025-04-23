package com.example.mapsfriends

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsfriends.ui.login.LoginScreen

@Composable
fun App(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("main") }
            )
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
        composable("profile") { ProfileScreen(navController) }
    }
}
