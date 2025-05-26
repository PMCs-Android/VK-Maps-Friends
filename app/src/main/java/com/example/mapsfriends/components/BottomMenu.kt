package com.example.mapsfriends.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsfriends.R

@Composable
fun BottomMenu(
    navController: NavHostController,
    onCenterLocation: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CenteringGeoPositionButton(onCenter = onCenterLocation)
            Spacer(modifier = Modifier.height(10.dp))
            BottomMenuButtons(navController)
        }
    }
}

@Composable
fun CenteringGeoPositionButton(
    onCenter: () -> Unit
) {
    IconButton(
        onClick = onCenter,
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(
                4.dp,
                colorResource(R.color.main_purple),
                RoundedCornerShape(16.dp)
            )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.gps_focus),
            contentDescription = "Location",
            tint = colorResource(R.color.main_purple)
        )
    }
}

@Composable
fun ChatButton(navController: NavHostController) {
    IconButton(
        onClick = { navController.navigate("chats") },
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(
                4.dp,
                colorResource(R.color.main_blue),
                RoundedCornerShape(12.dp)
            )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.chat_main_page),
            contentDescription = "Chats",
            tint = colorResource(R.color.main_blue)
        )
    }
}

@Composable
fun FriendsButton(navController: NavHostController) {
    IconButton(
        onClick = { navController.navigate("friends") },
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(
                4.dp,
                colorResource(R.color.main_pink),
                RoundedCornerShape(12.dp)
            )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.friends),
            contentDescription = "Friends",
            tint = colorResource(R.color.main_pink)
        )
    }
}

@Composable
fun BottomMenuButtons(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChatButton(navController)
        Spacer(modifier = Modifier.width(10.dp))
        EventButton(navController)
        Spacer(modifier = Modifier.width(10.dp))
        FriendsButton(navController)
    }
}

@Composable
fun EventButton(
    navController: NavHostController
) {
    IconButton(
        onClick = { navController.navigate("events") },
        modifier = Modifier
            .border(
                4.dp,
                colorResource(R.color.main_purple),
                RoundedCornerShape(12.dp)
            )
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
} 