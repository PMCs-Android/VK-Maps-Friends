package com.example.mapsfriends

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun FriendsScreen(
    navController: NavHostController
) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val friends by userViewModel.friends.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.startObservingUserFriends()
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
            .padding(horizontal = 10.dp, vertical = 30.dp)
    ) {
        FriendsScreenHeader(navController)
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(friends) { friend ->
                FriendRow(navController, friend)
            }
        }
    }
}

@Composable
fun FriendsScreenHeader(navController: NavHostController) {
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
            text = stringResource(R.string.my_friends),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun FriendRow(
    navController: NavHostController,
    friend: User
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
            .padding(horizontal = Dimensions.SMALL_PADDING_3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = friend.avatarUrl,
            contentDescription = "Friend Avatar",
            modifier = Modifier
                .size(Dimensions.MEDIUM_SPACING_4.dp)
                .clip(CircleShape)
                .clickable {
                    navController.navigate("profile/${friend.userId}")
                }
        )
        Text(
            text = friend.username,
            fontSize = Dimensions.SMALL_PADDING_3.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = Dimensions.SMALL_PADDING_3.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { navController.navigate("messenger") },
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(4.dp, colorResource(R.color.main_blue), RoundedCornerShape(12.dp))
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.chat_main_page),
                contentDescription = "Chats",
                tint = colorResource(R.color.main_blue)
            )
        }
    }
}
