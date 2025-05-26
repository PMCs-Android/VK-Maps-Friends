package com.example.mapsfriends.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsfriends.Dimensions
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
                .padding(bottom = Dimensions.MEDIUM_SPACING_2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CenteringGeoPositionButton(onCenter = onCenterLocation)
            Spacer(modifier = Modifier.height(Dimensions.SMALL_PADDING_1.dp))
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
            .background(Color.White, RoundedCornerShape(Dimensions.SMALL_PADDING_3.dp))
            .border(
                Dimensions.BORDER_WIDTH.dp,
                colorResource(R.color.main_purple),
                RoundedCornerShape(Dimensions.SMALL_PADDING_3.dp)
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
            .background(Color.White, RoundedCornerShape(Dimensions.SMALL_PADDING_2.dp))
            .border(
                Dimensions.BORDER_WIDTH.dp,
                colorResource(R.color.main_blue),
                RoundedCornerShape(Dimensions.SMALL_PADDING_2.dp)
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
            .background(Color.White, RoundedCornerShape(Dimensions.SMALL_PADDING_2.dp))
            .border(
                Dimensions.BORDER_WIDTH.dp,
                colorResource(R.color.main_pink),
                RoundedCornerShape(Dimensions.SMALL_PADDING_2.dp)
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
        Spacer(modifier = Modifier.width(Dimensions.SMALL_PADDING_1.dp))
        EventButton(navController)
        Spacer(modifier = Modifier.width(Dimensions.SMALL_PADDING_1.dp))
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
                Dimensions.BORDER_WIDTH.dp,
                colorResource(R.color.main_purple),
                RoundedCornerShape(Dimensions.SMALL_PADDING_2.dp)
            )
            .background(Color.White, RoundedCornerShape(Dimensions.SMALL_PADDING_2.dp))
            .padding(
                horizontal = Dimensions.SMALL_PADDING_1.dp,
                vertical = Dimensions.SMALL_PADDING_3.dp
            )
            .height(Dimensions.MAIN_BUTTON.dp)
            .width(Dimensions.MAIN_BUTTON.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.event_calendar),
            contentDescription = "Events",
            tint = colorResource(R.color.main_purple)
        )
    }
}
