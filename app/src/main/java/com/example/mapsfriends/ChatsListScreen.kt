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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ChatsListScreen(navController: NavHostController) {
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
            .padding(vertical = 30.dp)
    ) {
        ChatsHeader(navController)
        LazyColumn(
            modifier = Modifier.weight(1f).padding(top = 10.dp)
        ) {
            items(mockEvents) { event ->
                OneChat(navController, event)
            }
        }
    }
}

@Composable
fun ChatsHeader(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
    ) {
        IconButton(
            onClick = { navController.navigate("main") }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cross),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = "Чаты",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun OneChat(navController: NavHostController, event: MockDataEvents) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 10.dp)
            .height(80.dp)
            .clickable {
                navController.navigate("messenger")
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .width(50.dp)
                .background(colorResource(R.color.main_purple), RoundedCornerShape(12.dp)),
        ) {
            Text(
                text = event.name[0].toString(),
                fontWeight = FontWeight(700),
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = event.name,
                fontWeight = FontWeight(500),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = event.members[0].name + ": ",
                    fontSize = 12.sp,
                    color = colorResource(R.color.main_purple)
                )
                Text(
                    text = mockMessages[0].text,
                    fontSize = 12.sp,
                    color = colorResource(R.color.text_gray)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}
