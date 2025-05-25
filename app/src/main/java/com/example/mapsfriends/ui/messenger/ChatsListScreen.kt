package com.example.mapsfriends.ui.messenger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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

@Composable
fun ChatsListScreen(
    viewModel: ChatsListViewModel = hiltViewModel(),
    onChatClick: (String) -> Unit,
    onNewChatClick: () -> Unit
) {
    val chats by viewModel.chats.collectAsState()
    val users by viewModel.users.collectAsState()

    Column {
        Button(onClick = onNewChatClick) {
            Text("Start New Chat")
        }
        LazyColumn {
            items(chats) { chat ->
                val otherUserId = chat.participants.firstOrNull { it != viewModel.currentUserId }
                val user = otherUserId?.let { users[it] }
                ChatItem(
                    username = user?.username ?: "Unknown",
                    avatarUrl = user?.avatarUrl ?: "",
                    lastMessage = chat.lastMessage,
                    lastMessageTime = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault())
                        .format(chat.lastMessageTime.toDate()),
                    onClick = { onChatClick(chat.chatId) }
                )
            }
        }
    }
}

@Composable
fun ChatItem(
    username: String,
    avatarUrl: String,
    lastMessage: String,
    lastMessageTime: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier.size(48.dp)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = username, style = MaterialTheme.typography.titleMedium)
            Text(
                text = lastMessage.takeIf { it.isNotBlank() } ?: "No messages yet",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = lastMessageTime, style = MaterialTheme.typography.bodySmall)
        }
    }
}