package com.example.mapsfriends.ui.messenger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mapsfriends.messenger.ChatsListViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import com.example.mapsfriends.Dimensions
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mapsfriends.DateTimePickers
import com.example.mapsfriends.Event
import com.example.mapsfriends.MockDataEvents
import com.example.mapsfriends.R
import com.example.mapsfriends.User
import com.example.mapsfriends.messenger.Chat
import com.example.mapsfriends.mockMessages

@Composable
fun ChatsListScreen(
    viewModel: ChatsListViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val chats by viewModel.chats.collectAsState()
    val users by viewModel.users.collectAsState()
    val events by viewModel.events.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(R.color.bg_blue),
                        colorResource((R.color.bg_pink))
                    )
                )
            )
            .padding(
                horizontal = Dimensions.SMALL_PADDING_1.dp,
                vertical = Dimensions.MEDIUM_SPACING_2.dp
            )
    ) {
        ChatsHeader(navController)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = Dimensions.SMALL_PADDING_1.dp)
        ) {
            items(chats) { chat ->
                val otherUserId = chat.participants.firstOrNull { it != viewModel.currentUserId }
                val user = otherUserId?.let { users[it] }
                OneChat(
                    chat = chat,
                    users = users,
                    events = events,
                    currentUserId = viewModel.currentUserId,
                    onClick = { navController.navigate("chat/${chat.chatId}") }
                )
            }
        }
    }
}

@Composable
fun ChatsHeader(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxWidth()
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
            fontSize = DateTimePickers.DEFAULT_PADDING.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick = { navController.navigate("new_chat") },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.add_plus),
                contentDescription = "New Chat",
                tint = Color.White
            )
        }
    }
}

@Composable
fun OneChat(
    chat: Chat,
    users: Map<String, User>,
    events: Map<String, Event>,
    currentUserId: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(DateTimePickers.LARGE_DIALOG.dp)
            .padding(vertical = Dimensions.SMALL_PADDING_1.dp)
            .background(
                Color.White,
                RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp)
            )
            .padding(horizontal = Dimensions.SMALL_PADDING_3.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
//        Box(
//            modifier = Modifier
//                .height(Dimensions.MEDIUM_SPACING_4.dp)
//                .width(Dimensions.MEDIUM_SPACING_4.dp)
//        ) {
//            AsyncImage(
//                model = avatar,
//                contentDescription = "Avatar",
//                modifier = Modifier
//                    .size(Dimensions.MEDIUM_SPACING_4.dp)
//                    .clip(CircleShape)
//            )
//        }
        if (chat.isGroupChat) {
            val event = events[chat.eventId ?: ""]
            val firstLetter = event?.title?.take(1)?.uppercase() ?: "?"
            Box(
                modifier = Modifier
                    .size(Dimensions.MEDIUM_SPACING_4.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.main_purple)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = firstLetter,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        } else {
            // Для личного чата показываем аватарку
            val otherUserId = chat.participants.firstOrNull { it != currentUserId }
            val user = otherUserId?.let { users[it] }
            AsyncImage(
                model = user?.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(Dimensions.MEDIUM_SPACING_4.dp)
                    .clip(CircleShape),)
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
//            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (chat.isGroupChat) {
                    events[chat.eventId ?: ""]?.title ?: "Групповой чат"
                } else {
                    val otherUserId = chat.participants.firstOrNull { it != currentUserId }
                    users[otherUserId]?.username ?: "Неизвестный"
                },
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = chat.lastMessage.takeIf { it.isNotBlank() } ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = if (chat.lastMessage.isBlank()) "" else SimpleDateFormat(
                    "HH:mm, dd MMM",
                    Locale.getDefault()
                ).format(chat.lastMessageTime.toDate()),
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(R.color.main_purple)
            )
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}