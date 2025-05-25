package com.example.mapsfriends.ui.messenger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mapsfriends.messenger.NewChatViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatScreen(
    viewModel: NewChatViewModel = hiltViewModel(),
    onChatCreated: (String) -> Unit,
    onError: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val friends by viewModel.friends.collectAsState()

    Column {
        TopAppBar(
            title = { Text("Select Friend") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        LazyColumn {
            items(friends) { friend ->
                FriendItem(
                    username = friend.username,
                    avatarUrl = friend.avatarUrl,
                    onClick = {
                        viewModel.createChat(
                            friendId = friend.userId,
                            onSuccess = onChatCreated,
                            onError = onError
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FriendItem(
    username: String,
    avatarUrl: String,
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
        Text(
            text = username,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}