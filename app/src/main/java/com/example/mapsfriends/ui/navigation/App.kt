package com.example.mapsfriends.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mapsfriends.RequestsScreen
import com.example.mapsfriends.ui.components.MapScreen
import com.example.mapsfriends.ui.screens.EventCalendarScreen
import com.example.mapsfriends.ui.screens.EventDetailsScreen
import com.example.mapsfriends.ui.screens.MainScreen
import com.example.mapsfriends.ui.screens.MessengerScreen
import com.example.mapsfriends.ui.screens.ProfileScreen
import com.example.mapsfriends.ui.screens.RequestDetailsScreen

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = "main"
    ) {
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
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            ProfileScreen(
                navController = navController,
                id = backStackEntry.arguments?.getInt("userId") ?: 0
            )
        }
    }
}
