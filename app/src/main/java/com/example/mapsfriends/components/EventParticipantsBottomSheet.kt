package com.example.mapsfriends.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mapsfriends.Dimensions
import com.example.mapsfriends.EventViewModel
import com.example.mapsfriends.R
import com.example.mapsfriends.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventParticipantsBottomSheet(
    showSheet: MutableState<Boolean>,
    eventViewModel: EventViewModel,
    currentEvent: com.example.mapsfriends.Event
) {
    LaunchedEffect(Unit) {
        eventViewModel.loadAvailableFriends()
    }

    val availableFriends by eventViewModel.availableFriends.collectAsState()

    ModalBottomSheet(
        onDismissRequest = { showSheet.value = false },
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.White
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    min = Dimensions.LARGE_ELEMENT_3.dp,
                    max = Dimensions.LARGE_ELEMENT_4.dp
                )
                .padding(Dimensions.SMALL_PADDING_3.dp)
        ) {
            // Показываем текущих участников
            items(currentEvent.participants) { participantId ->
                ParticipantRow(
                    user = eventViewModel.participants.value.find { it.userId == participantId },
                    isParticipant = true,
                    onAction = { }
                )
            }

            // Показываем приглашенных пользователей
            items(currentEvent.invites) { invitedId ->
                ParticipantRow(
                    user = eventViewModel.availableFriends.value.find { it.userId == invitedId },
                    isPending = true,
                    onAction = { }
                )
            }

            // Показываем доступных для приглашения друзей
            items(availableFriends) { friend ->
                ParticipantRow(
                    user = friend,
                    onAction = { eventViewModel.inviteFriendToEvent(friend.userId) }
                )
            }
        }
    }
}

@Composable
private fun ParticipantRow(
    user: User?,
    isParticipant: Boolean = false,
    isPending: Boolean = false,
    onAction: () -> Unit
) {
    if (user == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.LARGE_ELEMENT_1.dp)
            .padding(top = Dimensions.SMALL_PADDING_1.dp)
            .background(
                Color.White,
                RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp)
            ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(Dimensions.MEDIUM_SPACING_4.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            text = user.username,
            fontSize = Dimensions.SMALL_PADDING_3.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Dimensions.SMALL_PADDING_1.dp)
        )
        when {
            isParticipant -> {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.acception),
                    contentDescription = "Participant",
                    tint = colorResource(R.color.main_blue),
                    modifier = Modifier.size(24.dp)
                )
            }
            isPending -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = colorResource(R.color.main_purple)
                )
            }
            else -> {
                IconButton(onClick = onAction) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.add_plus),
                        contentDescription = "Invite friend",
                        tint = colorResource(R.color.main_purple)
                    )
                }
            }
        }
    }
} 