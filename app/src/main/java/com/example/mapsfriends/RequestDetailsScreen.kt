package com.example.mapsfriends

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun RequestDetailsScreen(
    navController: NavHostController,
    eventId: String,
    userViewModel: UserViewModel = hiltViewModel(),
    viewModel: EventViewModel = hiltViewModel()
) {

    val avatars = viewModel.avatars.collectAsState().value
    LaunchedEffect(eventId) {
        viewModel.getEvent(eventId)
    }
    val event = viewModel.currentEvent.collectAsState().value

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
        if (event != null) {
            EventHeader(navController, event, avatars as Map<String, String>)
            EventDescription(event)
            EventMembers(avatars)
            EventLocation()
            RequestAcceptRefuseButtons()
        }
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
            onClick = { /* Принять заявку */ },
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
            onClick = { /* Отклонить заявку */ },
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
