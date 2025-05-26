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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun RequestDetailsScreen(
    navController: NavHostController,
    requestId: String,
    viewModel: EventViewModel = hiltViewModel()
) {

    val avatars = viewModel.avatars.collectAsState().value
    LaunchedEffect(requestId) {
        viewModel.getEvent(requestId)
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
            RequestDetailsHeader(navController, event)
            RequestDetailsDescription(event)
            RequestDetailsMembers(avatars as Map<String, String>)
            EventLocation(event = event)
            RequestAcceptRefuseButtons(
                event = event,
                navController = navController
            )

        }
    }
}

@Composable
fun RequestDetailsHeader(
    navController: NavHostController,
    request: Event
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(0.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = request.title,
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(3f).align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = request.time.slice(0..4),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = request.time.slice(Dimensions.SIX..10),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun RequestDetailsDescription(request: Event) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(80.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
    ) {
        Text(
            text = request.description,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(Dimensions.SMALL_PADDING_1.dp)
        )
    }
}

@Composable
fun RequestDetailsMembers(
    avatars: Map<String, String>
) {
    Row(
        modifier = Modifier
            .height(Dimensions.MEDIUM_SPACING_4.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp)
    ) {
        avatars.forEach { member ->
            AsyncImage(
                model = member.value,
                contentDescription = "Participant Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
fun RequestAcceptRefuseButtons(
    viewModel: InvitesViewModel = hiltViewModel(),
    event: Event,
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(Dimensions.SMALL_PADDING_1.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    viewModel.acceptInvite(event.eventId)
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .width(64.dp)
                .border(
                    Dimensions.TWO.dp,
                    colorResource(R.color.main_blue),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.acception),
                contentDescription = "Accept",
                tint = colorResource(R.color.main_blue)
            )
        }
        IconButton(
            onClick = {
                coroutineScope.launch {
                    viewModel.declineInvite(event.eventId)
                    navController.popBackStack()
                }
            },
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
