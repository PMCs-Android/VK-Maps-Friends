package com.example.mapsfriends.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsfriends.R

@Composable
fun RequestDetailsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(R.color.bg_blue),
                        colorResource(R.color.bg_pink)
                    )
                )
            )
            .padding(vertical = 30.dp, horizontal = 10.dp)
    ) {
        EventHeader(navController)
        EventDescription()
        EventMembers()
        EventLocation()
        RequestAcceptRefuseButtons()
    }
}

@Composable
fun RequestAcceptRefuseButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {/* Принять заявку */ },
            modifier = Modifier
                .width(64.dp)
                .border(2.dp, colorResource(R.color.main_blue), RoundedCornerShape(16.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.acception),
                contentDescription = "Accept",
                tint = colorResource(R.color.main_blue)
            )
        }
        IconButton(
            onClick = {/* Отклонить заявку */ },
            modifier = Modifier
                .width(64.dp)
                .border(2.dp, colorResource(R.color.main_pink), RoundedCornerShape(16.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cross),
                contentDescription = "Refuse",
                tint = colorResource(R.color.main_pink)
            )
        }
    }
}
