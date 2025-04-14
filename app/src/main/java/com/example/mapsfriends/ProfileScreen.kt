package com.example.mapsfriends

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage


@Composable
fun ProfileScreen(
    navController: NavHostController,
    id: String,
    viewModel: MapViewModel = hiltViewModel()
) {
    val user = viewModel.selectedUser.collectAsState().value
    if (user == null) {
        LoadingView()
        viewModel.getUser(id)
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
            contentDescription = "Profile Picture",
            modifier = Modifier
                .height(400.dp)
                .width(400.dp)
                .align(Alignment.CenterHorizontally),
            placeholder = painterResource(R.drawable.default_avatar),
            error = painterResource(R.drawable.default_avatar),
            fallback = painterResource(R.drawable.default_avatar)
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
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(text = "Загрузка профиля...", color = Color.White)
    }
}
