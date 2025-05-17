package com.example.mapsfriends

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.mapsfriends.login.AuthViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

data class MarkerData(
    val id: String,
    val position: LatLng,
    val title: String,
    val originalBitmap: Bitmap,
    var icon: BitmapDescriptor? = null
)

@Composable
fun MainScreen(
    navController: NavHostController,
    tokenManager: AuthViewModel = hiltViewModel()
) {
    val isUserRegistered by tokenManager.isUserRegistered.collectAsState()

    LaunchedEffect(Unit) {
        tokenManager.checkUserInFirebase()
    }

    when (isUserRegistered) {
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Проверка данных пользователя...")
                }
            }
        }
        true -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 30.dp)
            ) {
                MapScreen(navController)

                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier
                            .background(colorResource(R.color.white), RoundedCornerShape(12.dp))
                            .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.profile),
                            contentDescription = "Profile",
                            tint = colorResource(R.color.main_purple)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    IconButton(
                        onClick = { /* Показать статистику шагов */ },
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.footstep_boot),
                            contentDescription = "Footsteps",
                            tint = colorResource(R.color.main_purple)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 30.dp)
                ) {
                    IconButton(
                        onClick = { /* Центрировать геоположение */ },
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(16.dp))
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.gps_focus),
                            contentDescription = "Location",
                            tint = colorResource(R.color.main_purple)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { navController.navigate("messenger") },
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(4.dp, colorResource(R.color.main_blue), RoundedCornerShape(12.dp))
                                .align(Alignment.CenterVertically)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.chat_main_page),
                                contentDescription = "Chats",
                                tint = colorResource(R.color.main_blue)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))

                        IconButton(
                            onClick = { navController.navigate("events") },
                            modifier = Modifier
                                .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(12.dp))
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 16.dp)
                                .height(56.dp)
                                .width(56.dp)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.event_calendar),
                                contentDescription = "Events",
                                tint = colorResource(R.color.main_purple)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = { navController.navigate("friends") },
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(4.dp, colorResource(R.color.main_pink), RoundedCornerShape(12.dp))
                                .align(Alignment.CenterVertically)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.friends),
                                contentDescription = "Friends",
                                tint = colorResource(R.color.main_pink)
                            )
                        }
                    }
                }
            }
        }
        false -> {
            LaunchedEffect(Unit) {
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Перенаправление на страницу входа...")
            }
        }
    }
}
