package com.example.mapsfriends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddParticipantsScreen(
    eventViewModel: EventViewModel,
    showAddFriend: MutableState<Boolean>
) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val friends by userViewModel.friends.collectAsState()
    LaunchedEffect(Unit) {
        userViewModel.loadFriends(currentUser.userId)
    }
    ModalBottomSheet(
        onDismissRequest = {
            showAddFriend.value = false
        },
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.White
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 400.dp, max = 800.dp)
                .padding(16.dp)
        ) {
            items(friends) { friend ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(top = 10.dp)
                        .background(Color.White, RoundedCornerShape(20.dp)),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = friend.avatarUrl,
                        contentDescription = "Friend Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = friend.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                    )
                    IconButton(
                        onClick = {
                            eventViewModel.addParticipant(friend.userId)
                        },
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.add_plus),
                            contentDescription = "Add friend",
                            tint = colorResource(R.color.main_purple)
                        )
                    }
                }
            }
        }
    }
}
