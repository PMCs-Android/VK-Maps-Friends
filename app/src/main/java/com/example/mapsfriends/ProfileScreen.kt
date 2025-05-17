package com.example.mapsfriends

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.mapsfriends.login.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: MapViewModel = hiltViewModel(),
    tokenManager: AuthViewModel = hiltViewModel(),
) {
    val user = viewModel.selectedUser.collectAsState().value

    val id = tokenManager.getCurrentUserId()
    if (id == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
        return
    }

    LaunchedEffect(id) {
        viewModel.getUser(id)
    }

    if (user == null) {
        LoadingView()
        viewModel.getUser(id)
        return
    }

    val menu = remember { mutableStateOf(false) }

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
            .padding(vertical = 30.dp, horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .border(
                        4.dp,
                        Color.White,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.cross),
                    contentDescription = "Delete",
                    tint = Color.White,
                )
            }

            Box {
                IconButton(onClick = { menu.value = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_more_vert_24),
                        contentDescription = "More options",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = menu.value,
                    onDismissRequest = { menu.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Выйти из аккаунта") },
                        onClick = {
                            menu.value = false
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                            tokenManager.logout()
                        }
                    )
                }
            }
        }
//        Icon(
//            imageVector = Icons.Default.AccountCircle,
//            contentDescription = "Profile Icon",
//            tint = Color.Cyan,
//            modifier = Modifier
//                .height(240.dp)
//                .width(240.dp)
//                .align(Alignment.CenterHorizontally)
//        )
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "Friend Avatar",
            modifier = Modifier
                .height(Dimensions.PROFILE_ICON_SIZE.dp)
                .width(Dimensions.PROFILE_ICON_SIZE.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
        )
        Text(
            text = user.username,
            fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
@Composable
fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Загрузка профиля...", color = Color.White)
    }
}

@Composable
fun FriendProfileScreen(
    navController: NavHostController,
    viewModel: MapViewModel = hiltViewModel(),
    userId: String
) {
    val user = viewModel.selectedUser.collectAsState().value

    androidx.compose.runtime.LaunchedEffect(userId) {
        viewModel.getUser(userId)
    }

    if (user == null) {
        LoadingView()
        viewModel.getUser(userId)
        return
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
            .padding(vertical = 30.dp, horizontal = 10.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .border(
                    4.dp,
                    Color.White,
                    RoundedCornerShape(16.dp)
                )

        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cross),
                contentDescription = "Delete",
                tint = Color.White,
            )
        }
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "Friend Avatar",
            modifier = Modifier
                .height(Dimensions.PROFILE_ICON_SIZE.dp)
                .width(Dimensions.PROFILE_ICON_SIZE.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
        )
        Text(
            text = user.username,
            fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
