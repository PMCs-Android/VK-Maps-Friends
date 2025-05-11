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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun EventDetailsScreen(
    navController: NavHostController,
    eventId: String
) {
    val viewModel = hiltViewModel<EventViewModel>()
    LaunchedEffect(eventId) {
        viewModel.getEvent(eventId)
    }
    val event = viewModel.currentEvent.collectAsState().value
    val avatars = viewModel.avatars.collectAsState().value

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
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                EventMembers(avatars)
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = { /* ереход в чат */ navController.navigate("messenger") },
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
            EventDeleteButton(viewModel, event, navController)
        }
    }
}

@Composable
fun EventHeader(
    navController: NavHostController,
    event: Event,
    avatars: Map<String, String>
) {
    val eventDate = parseEventDate(event.time)
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
            text = event.title,
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(3f).align(Alignment.CenterVertically)
        )
        if (avatars.containsKey(event.creatorId)) {
            //Text(text = "!" + avatars[event.creatorId])
            AsyncImage(
                model = avatars[event.creatorId],
                contentDescription = "Creator Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Default Avatar",
                tint = colorResource(R.color.bg_pink)
            )
        }
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = eventDate["day"] + " " + monthList[eventDate["month"]!!.toInt() - 1],
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = eventDate["time"]!!,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun EventDescription(event: Event) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(80.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
    ) {
        Text(
            text = event.description,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(Dimensions.SMALL_PADDING_1.dp)
        )
    }
}

@Composable
fun EventMembers(
    avatars: Map<String, String>
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp)
    ) {
        avatars.forEach { member ->
            AsyncImage(
                model = member.value,
                contentDescription = "Participant Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
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
fun EventDeleteButton(viewModel: EventViewModel, event: Event, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(
            onClick = { /* Удаление ивента */
                viewModel.deleteEvent(event.eventId)
                navController.navigate("events")
            },
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
