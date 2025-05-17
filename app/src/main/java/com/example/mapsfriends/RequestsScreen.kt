package com.example.mapsfriends

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun RequestsScreen(
    navController: NavHostController,
    viewModel: InvitesViewModel = hiltViewModel()
) {
    val invites by viewModel.invitesFlow.collectAsState()
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
        RequestsHeader(navController)
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(invites) { event ->
                OneRequest(navController, event)
            }
        }
    }
}

@Composable
fun RequestsHeader(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { navController.popBackStack() }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = stringResource(R.string.requests),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun RequestButtons(
    eventId: String,
    viewModel: InvitesViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
    ) {
        IconButton(
            onClick = { /* Принятие приглашения */
                viewModel.acceptInvite(eventId)
            },
            modifier = Modifier
                .border(
                    2.dp,
                    colorResource(R.color.main_blue),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.acception),
                contentDescription = "Delete",
                tint = colorResource(R.color.main_blue),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        IconButton(
            onClick = { /* Удаление приглашения */
                viewModel.declineInvite(eventId)
            },
            modifier = Modifier
                .border(
                    2.dp,
                    colorResource(R.color.main_pink),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.delete_cross),
                contentDescription = "Delete",
                tint = colorResource(R.color.main_pink),
            )
        }
    }
}

@Composable
fun OneRequest(
    navController: NavHostController,
    event: Event,
    userViewModel: UserViewModel = hiltViewModel(),
) {

    val eventDate = parseEventDate(event.time)

    LaunchedEffect(event) {
        if (!userViewModel.avatarsPerEvent.value.containsKey(event.eventId)) {
            userViewModel.loadAvatarsForEventCard(event.eventId, event.participants)
        }
    }
    val avatars = userViewModel.avatarsPerEvent.collectAsState().value[event.eventId] ?: emptyMap()
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("requestDetails/${event.eventId}") }
                .weight(1f)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .height(60.dp)
                    .width(60.dp)
                    .background(colorResource(R.color.light_purple), RoundedCornerShape(12.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = event.time.slice(0..1),
                    fontWeight = FontWeight(800),
                    fontSize = 24.sp,
                    color = colorResource(R.color.main_purple)
                )
                Text(
                    text = monthList[event.time.slice(3..4).toInt() - 1],
                    fontWeight = FontWeight(500),
                    fontSize = 16.sp,
                    color = colorResource(R.color.main_purple)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
                Row {
                    avatars.forEach { member ->
                        AsyncImage(
                            model = member.value,
                            contentDescription = "Friend Avatar",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
                Text(
                    text = event.description,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = getWeekdayFromDate(
                        event.time.slice(0..1).toInt(),
                        event.time.slice(3..4).toInt()
                    ) +
                        ", " + event.time.slice(6..10),
                    fontSize = 12.sp,
                )
            }
            RequestButtons(event.eventId)
        }
    }
}

@Composable
fun EventAvatarsRow(avatars: Map<String, String>, total: Int, invited: Int) {
    Row {
        avatars.forEach { participantAvatar ->
            AsyncImage(
                model = participantAvatar.value,
                contentDescription = "Friend Avatar",
                modifier = Modifier.size(24.dp)
                    .clip(CircleShape)
            )
        }

        Text(
            text = "$total/$invited",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.align(Alignment.CenterVertically).padding(
                horizontal = 4.dp
            )
        )
    }
}
