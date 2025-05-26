package com.example.mapsfriends.ui.messenger

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.mapsfriends.DateTimePickers
import com.example.mapsfriends.Dimensions
import com.example.mapsfriends.R
import com.example.mapsfriends.ui.theme.MessageColor

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    chatId: String,
    navController: NavHostController
) {
    val messages by viewModel.messages.collectAsState()
    val otherUser by viewModel.otherUser.collectAsState()
    val event by viewModel.event.collectAsState()
    val currentChat by viewModel.currentChat.collectAsState()
    val users by viewModel.users.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        viewModel.loadChat(chatId)
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

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
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    horizontal = Dimensions.SMALL_PADDING_1.dp,
                    vertical = Dimensions.MEDIUM_SPACING_2.dp
                )
        ) {
            ChatHeader(
                navController = navController,
                chatName = if (currentChat?.isGroupChat == true) event?.title ?: "Chat"
                           else otherUser?.username ?: "Chat",
                avatarUrl = if (currentChat?.isGroupChat == true) null else otherUser?.avatarUrl,
                isGroupChat = currentChat?.isGroupChat ?: false
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(messages) { message ->
                    MessageItem(
                        message = message,
                        isOwnMessage = message.senderId == viewModel.currentUserId,
                        isGroupChat = currentChat?.isGroupChat ?: false,
                        senderAvatarUrl = if (currentChat?.isGroupChat == true) users[message.senderId]?.avatarUrl else null
                    )
                }
            }
        }
        MessageInput(
            messageText = messageText,
            onMessageTextChange = { messageText = it },
            onSendClick = {
                if (messageText.isNotBlank()) {
                    viewModel.sendMessage(chatId, messageText)
                    messageText = ""
                }
            }
        )
    }
}

@Composable
fun ChatHeader(
    navController: NavHostController,
    chatName: String,
    avatarUrl: String?,
    isGroupChat: Boolean
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = chatName,
            fontSize = DateTimePickers.DEFAULT_PADDING.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
        if (!isGroupChat && avatarUrl != null) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Friend Avatar",
                modifier = Modifier
                    .size(Dimensions.MEDIUM_SPACING_2.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    isOwnMessage: Boolean,
    isGroupChat: Boolean,
    senderAvatarUrl: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        if (isGroupChat && !isOwnMessage && senderAvatarUrl != null) {
            AsyncImage(
                model = senderAvatarUrl,
                contentDescription = "Sender Avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .padding(end = 8.dp),
            )
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isOwnMessage) MessageColor else Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = message.text, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(message.timestamp.toDate()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun MessageInput(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                top = Dimensions.SMALL_PADDING_1.dp,
                start = Dimensions.SMALL_PADDING_1.dp,
                end = Dimensions.SMALL_PADDING_1.dp,
                bottom = Dimensions.MEDIUM_SPACING_3.dp
            )
    ) {
        TextField(
            value = messageText,
            onValueChange = onMessageTextChange,
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
            onClick = onSendClick,
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