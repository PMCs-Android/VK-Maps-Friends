package com.example.mapsfriends

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun EventDetailsScreen(navController: NavHostController) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            EventMembers()
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = { navController.navigate("messenger") },
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(20.dp))
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.event_chat),
                    contentDescription = "Event chat",
                    tint = colorResource(R.color.main_purple)
                )
            }
        }
        EventLocation()
        EventDeleteButton()
    }
}

@Composable
fun EventHeader(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(0.dp)
                .border(4.dp, Color.White, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = mockEvents[0].name,
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(3f)
                .align(Alignment.CenterVertically)
        )
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "CreatorIcon",
            modifier = Modifier
                .width(36.dp)
                .height(36.dp)
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = "${mockEvents[0].day} ${monthList[mockEvents[0].month - 1]}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = mockEvents[0].time,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun EventDescription() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(80.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
    ) {
        Text(
            text = mockEvents[0].description,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(10.dp)
        )
    }
}

@Composable
fun EventMembers() {
    Row(
        modifier = Modifier
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(start = 10.dp)
    ) {
        mockEvents[0].members.forEach { member ->
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "MemberIcon",
                tint = colorResource(R.color.bg_pink),
                modifier = Modifier
                    .width(36.dp)
                    .height(36.dp)
                    .align(Alignment.CenterVertically)
            )
        }
        IconButton(
            onClick = { /* Добавить человека */ },
            modifier = Modifier
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.add_plus),
                contentDescription = "Add member",
                tint = colorResource(R.color.main_purple)
            )
        }
    }
}

@Composable
fun EventLocation() {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .height(300.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
    ) {
        Text(
            text = mockEvents[0].location,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        )
        Box(
            modifier = Modifier
                .padding(10.dp)
        ) {
            // MapScreen()
        }
    }
}

@Composable
fun EventDeleteButton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(
            onClick = { /* Удаление ивента */ },
            modifier = Modifier
                .border(4.dp, colorResource(R.color.main_pink), RoundedCornerShape(20.dp))
        ) {
            Text(
                text = LocalContext.current.getString(R.string.dalete),
                fontSize = 20.sp,
                color = colorResource(R.color.main_pink),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
