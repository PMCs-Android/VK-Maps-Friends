package com.example.mapsfriends

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddParticipantsScreen(
    eventViewModel: EventViewModel,
    showAddFriend: MutableState<Boolean>
) {
    val context = LocalContext.current

    val userViewModel = hiltViewModel<UserViewModel>()

    LaunchedEffect(Unit) {
        userViewModel.loadFriends(currentUser.userId)
    }

    val friends by userViewModel.friends.collectAsState()

    // Эффект для обновления при закрытии
    LaunchedEffect(showAddFriend.value) {
        if (!showAddFriend.value) {
            eventViewModel.loadParticipants()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            showAddFriend.value = false
        },
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.White
    ) {
        Text(
            text = currentUser.userId
        )
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
                            Toast.makeText(
                                context,
                                "Участников: ${eventViewModel.participants.value.size}",
                                Toast.LENGTH_SHORT
                            ).show()
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

// @Composable
// fun AddParticipantsScreen(navController: NavHostController) {
//    val userViewModel  = hiltViewModel<UserViewModel>()
//    val eventViewModel  = hiltViewModel<EventViewModel>()
//    val friends = userViewModel.friends.collectAsState().value
//
//    LaunchedEffect(Unit) {
//        // CHANGE TO REAL CURRENT USER ID !!
//        userViewModel.loadFriends(currentUser.userId)
//
////        eventViewModel.setEventId(eventId)
//    }
//
//    val createdEventId = eventViewModel.eventId.collectAsState().value
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.horizontalGradient(
//                    colors = listOf(
//                        colorResource(R.color.bg_blue),
//                        colorResource(R.color.bg_pink)
//                    )
//                )
//            )
//            .padding(horizontal = 10.dp, vertical = 30.dp)
//    ) {
//        Box(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            IconButton(
//                onClick = { navController.popBackStack() },
//                modifier = Modifier
//                    .border(4.dp, Color.White, RoundedCornerShape(12.dp))
//            ) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
//                    contentDescription = "Back",
//                    tint = Color.White
//                )
//            }
//            Text(
//                text = "Друзья",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.White,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
//
//        Text(
//            text = createdEventId.toString()
//        )
//
//        LazyColumn(
//            modifier = Modifier.weight(1f)
//        ) {
//            items(friends) { friend ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(80.dp)
//                        .padding(top = 10.dp)
//                        .background(Color.White, RoundedCornerShape(20.dp)),
//                    horizontalArrangement = Arrangement.SpaceAround,
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    AsyncImage(
//                        model = friend.avatarUrl,
//                        contentDescription = "Friend Avatar",
//                        modifier = Modifier
//                            .size(48.dp)
//                    )
//                    Text(
//                        text = friend.username,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier
//                            .align(Alignment.CenterVertically)
//                            .padding(10.dp)
//                    )
//                    IconButton(
//                        onClick = {
//                            eventViewModel.addParticipant(createdEventId.toString(), friend.userId)
//                        },
//                        modifier = Modifier
//                    ) {
//                        Icon(
//                            imageVector = ImageVector.vectorResource(R.drawable.add_plus),
//                            contentDescription = "Add friend",
//                            tint = colorResource(R.color.main_purple)
//                        )
//                    }
//                }
//            }
//        }
//    }
// }
