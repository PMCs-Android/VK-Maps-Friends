package com.example.mapsfriends

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsfriends.ui.login.LoginScreen

@Composable
fun App(startDestination: String) {
    val navController = rememberNavController()
    
    // Обработка ошибок навигации
    LaunchedEffect(Unit) {
        try {
            Log.d("App", "Инициализация навигации с начальным экраном: $startDestination")
        } catch (e: Exception) {
            Log.e("App", "Ошибка при инициализации навигации", e)
        }
    }

    NavHost(
        navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { 
                    try {
                        navController.navigate("main") {
                            // Очищаем стек навигации, чтобы пользователь не мог вернуться на экран входа
                            popUpTo("login") { inclusive = true }
                        }
                    } catch (e: Exception) {
                        Log.e("App", "Ошибка при навигации на главный экран", e)
                    }
                }
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
