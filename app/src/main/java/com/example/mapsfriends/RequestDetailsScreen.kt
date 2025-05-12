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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun RequestDetailsScreen(navController: NavHostController, requestId: String) {
    val request = mockEvents[requestId.toInt()]
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
        RequestDetailsHeader(navController, request)
        RequestDetailsDescription(request)
        RequestDetailsMembers(request)
        EventLocation()
        RequestAcceptRefuseButtons()
    }
}

@Composable
fun RequestDetailsHeader(
    navController: NavHostController,
    request: MockDataEvents
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
            text = request.name,
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(3f).align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = "%02d.%02d".format(request.day, request.month),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = request.time,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun RequestDetailsDescription(request: MockDataEvents) {
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
fun RequestDetailsMembers(request: MockDataEvents) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp)
    ) {
        request.members.forEach { member ->
            AsyncImage(
                model = member.avatarUrl,
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
