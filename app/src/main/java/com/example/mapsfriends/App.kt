package com.example.mapsfriends

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mapsfriends.ui.login.LoginScreen

@RequiresApi(Build.VERSION_CODES.O)
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
        composable("chats") { ChatsListScreen(navController) }
        composable(
            route = "requestDetails/{requestId}",
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            RequestDetailsScreen(
                navController = navController,
                requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            )
        }
        composable("map") {
            MapScreen(navController = navController)
        }
        composable("profile") { ProfileScreen(navController) }
        composable("stepStatistic") { PedometerScreen(navController) }
    }
}
