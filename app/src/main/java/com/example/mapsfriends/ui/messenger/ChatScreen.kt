package com.example.mapsfriends.ui.messenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mapsfriends.messenger.ChatViewModel
import com.example.mapsfriends.messenger.Message
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.collectAsState
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    chatId: String,
    onBackClick: () -> Unit
) {
//    val messages by viewModel.messages.collectAsState()
//    val otherUser by viewModel.otherUser.collectAsState()
//    var messageText by remember { mutableStateOf("") }
//
//    LaunchedEffect(chatId) {
//        viewModel.loadChat(chatId)
//    }

    val messages by viewModel.messages.collectAsState()
    val otherUser by viewModel.otherUser.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(chatId) {
        isLoading = true
        errorMessage = null
        try {
            viewModel.loadChat(chatId)
        } catch (e: Exception) {
            errorMessage = "Ошибка загрузки чата: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column {
        TopAppBar(
            title = { Text(otherUser?.username ?: "Chat") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                MessageItem(
                    message = message,
                    isOwnMessage = message.senderId == viewModel.currentUserId
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(chatId, messageText)
                        messageText = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    isOwnMessage: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isOwnMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = message.text, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(message.timestamp.toDate()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}