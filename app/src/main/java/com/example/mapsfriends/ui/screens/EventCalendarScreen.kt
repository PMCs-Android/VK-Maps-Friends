package com.example.mapsfriends.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mapsfriends.MockDataEvents
import com.example.mapsfriends.R
import com.example.mapsfriends.mockEvents
import com.example.mapsfriends.mockUsers
import com.example.mapsfriends.monthList

@Composable
fun EventCalendarScreen(navController: NavHostController) {
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
            .padding(horizontal = 10.dp, vertical = 30.dp)
    ) {
        MyEventsHeader(navController)
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            NotEmptyEvents(navController)
        }
        BottomBar(navController)
    }
}

@Composable
fun MyEventsHeader(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .border(4.dp, Color.White, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cross),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = stringResource(R.string.my_events),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun OneEvent(event: MockDataEvents, navController: NavHostController) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = event.time,
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("eventDetails") }
                .weight(1f)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.name,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    event.members.forEach { member ->
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "MemberIcon",
                            tint = colorResource(R.color.main_purple)
                        )
                    }
                    Text(
                        text = "${event.members.size}/${mockUsers.size}",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
                Text(
                    text = "${event.day} ${monthList[event.month - 1]}",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = "~${event.time}",
                    fontSize = 12.sp
                )
            }
            DeleteEvent(modifier = Modifier)
        }
    }
}

@Composable
fun DeleteEvent(modifier: Modifier) {
    IconButton(
        onClick = { /* Удаление ивента */ },
        modifier = modifier
            .border(
                2.dp,
                colorResource(R.color.main_pink),
                RoundedCornerShape(16.dp)
            )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.cross),
            contentDescription = "Delete",
            tint = colorResource(R.color.main_pink)
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        TextButton(
            onClick = { navController.navigate("requests") },
            modifier = Modifier
                .height(64.dp)
                .width(104.dp)
                .border(4.dp, colorResource(R.color.main_blue), RoundedCornerShape(12.dp))
        ) {
            Text(
                text = stringResource(R.string.requests),
                fontSize = 20.sp,
                color = colorResource(R.color.main_blue),
                fontWeight = FontWeight.Bold
            )
        }
        TextButton(
            onClick = { /* Обновление данных ивентов */ },
            modifier = Modifier
                .height(64.dp)
                .width(104.dp)
                .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(12.dp))
        ) {
            Text(
                text = stringResource(R.string.my_events),
                fontSize = 20.sp,
                color = colorResource(R.color.main_purple),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        TextButton(
            onClick = { navController.navigate("create") },
            modifier = Modifier
                .height(64.dp)
                .width(104.dp)
                .border(4.dp, colorResource(R.color.main_pink), RoundedCornerShape(16.dp))
        ) {
            Text(
                text = stringResource(R.string.new_event),
                fontSize = 20.sp,
                color = colorResource(R.color.main_pink),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NotEmptyEvents(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        mockEvents.forEach { event ->
            Column {
                TextButton(
                    onClick = { /* Переход на день */ },
                    modifier = Modifier
                        .width(44.dp)
                        .height(60.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = event.day.toString(),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
                Text(
                    text = "вт",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
    LazyColumn {
        items(mockEvents) { event ->
            OneEvent(event, navController)
        }
    }
}
