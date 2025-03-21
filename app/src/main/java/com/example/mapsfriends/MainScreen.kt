package com.example.mapsfriends

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.BitmapDescriptor


data class MarkerData(
    val position: LatLng,
    val title: String,
    val originalBitmap: Bitmap,
    var icon: BitmapDescriptor? = null
)

@Composable
fun MainScreen(navController: NavHostController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 30.dp)
    ) {
        MapScreen()

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
            Row (
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                IconButton(
                    onClick = { /* Показать чаты */ },
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
                    onClick = { /* Показать друзей */ },
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

