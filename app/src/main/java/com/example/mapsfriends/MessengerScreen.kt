package com.example.mapsfriends

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MessengerScreen(navController: NavHostController) {
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
            .padding(top = 30.dp)
    ) {
        MessengerHeader(navController)
        Column(
            modifier = Modifier
                .weight(6f)
                .padding(horizontal = 10.dp)
        ) {
            mockMessages.forEach { message ->
                MessagesOutput(message)
            }
        }
        MessageInput()
    }
}

@Composable
fun MessageInput() {
    val message = remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .height(110.dp)
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        TextField(
            value = message.value,
            onValueChange = { message.value = it },
            textStyle = TextStyle(fontSize = 20.sp),
            modifier = Modifier
                .height(52.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                .align(Alignment.Top)
                .weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                focusedIndicatorColor = Color.White
            ),
            placeholder = {
                Text(
                    text = LocalContext.current.getString(R.string.message_placefolder),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            onClick = { /* Отправление сообщения */ },
            modifier = Modifier
                .background(colorResource(R.color.main_purple), CircleShape)
                .align(Alignment.Top)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.send_message),
                contentDescription = "Send message",
                tint = Color.White,
            )
        }
    }
}

@Composable
fun MessagesOutput(message: Messages) {
    Row {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Member Icon",
            tint = colorResource(R.color.main_purple),
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .height(80.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
        ) {
            Text(
                text = message.text,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(10.dp)
            )
            Text(
                text = message.time,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun MessengerHeader(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .border(4.dp, Color.White, RoundedCornerShape(12.dp))
                .align(Alignment.CenterVertically)
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
                .weight(4f)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = mockEvents[0].day.toString() + " " + monthList[mockEvents[0].month - 1],
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.End)
            )
            Text(
                text = mockEvents[0].time,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.End)
            )
            Row(
                modifier = Modifier.align(Alignment.End)
            ) {
                mockEvents[0].members.forEach {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Member Icon",
                        tint = colorResource(R.color.main_blue)
                    )
                }
            }
        }
    }
}
