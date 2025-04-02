package com.example.mapsfriends.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
=======
import androidx.compose.ui.graphics.Brush
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapsfriends.ui.profile.data.ProfileScreenDataObject
import com.example.mapsfriends.ui.theme.MainGradient

@Composable
<<<<<<< HEAD
fun UserProfileScreen(data: ProfileScreenDataObject) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MainGradient)
                .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
=======
fun UserProfileScreen(
    data: ProfileScreenDataObject
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
    ) {
        Text(
            text = "Данные пользователя",
            color = Color.White,
            fontSize = 24.sp,
<<<<<<< HEAD
            fontWeight = FontWeight.Bold,
=======
            fontWeight = FontWeight.Bold
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = data.email,
            color = Color.White,
            fontSize = 24.sp,
<<<<<<< HEAD
            fontWeight = FontWeight.Bold,
        )
    }
}
=======
            fontWeight = FontWeight.Bold
        )
    }
}
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
