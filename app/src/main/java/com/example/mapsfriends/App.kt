package com.example.mapsfriends

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@RequiresApi(Build.VERSION_CODES.O)
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
        composable("create") { CreateEventScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable(
            route = "eventDetails/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            EventDetailsScreen(
                navController = navController,
                eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            )
        }
        composable("messenger") { MessengerScreen(navController) }
        composable("requestDetails") { RequestDetailsScreen(navController) }
    }
}
