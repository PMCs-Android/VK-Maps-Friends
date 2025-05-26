package com.example.mapsfriends.ui.messenger

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mapsfriends.messenger.NewChatViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mapsfriends.DateTimePickers
import com.example.mapsfriends.Dimensions
import com.example.mapsfriends.R

@Composable
fun NewChatScreen(
    viewModel: NewChatViewModel = hiltViewModel(),
    onChatCreated: (String) -> Unit,
    navController: NavHostController
) {
    val friends by viewModel.friends.collectAsState()

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
        NewChatHeader(navController)
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(friends) { friend ->
                FriendRow(
                    username = friend.username,
                    avatarUrl = friend.avatarUrl,
                    onClick = {
                        viewModel.createChat(
                            friendId = friend.userId,
                            onSuccess = onChatCreated,
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun NewChatHeader(navController: NavHostController) {
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
            text = "Создать чат",
            fontSize = DateTimePickers.DEFAULT_PADDING.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun FriendRow(
    username: String,
    avatarUrl: String,
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
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Friend Avatar",
            modifier = Modifier
                .size(Dimensions.MEDIUM_SPACING_4.dp)
                .clip(CircleShape)
        )
        Text(
            text = username,
            fontSize = Dimensions.SMALL_PADDING_3.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = Dimensions.SMALL_PADDING_3.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}