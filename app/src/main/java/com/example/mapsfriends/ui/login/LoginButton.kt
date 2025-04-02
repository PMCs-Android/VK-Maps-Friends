package com.example.mapsfriends.ui.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
<<<<<<< HEAD
import com.example.mapsfriends.ui.theme.VKbutton
=======
import androidx.compose.ui.graphics.Color
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671

@Composable
fun LoginButton(
    text: String,
<<<<<<< HEAD
    onClick: () -> Unit,
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier.fillMaxWidth(0.5f),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = VKbutton,
            ),
    ) {
        Text(text = text)
    }
}
=======
    onClick: () -> Unit
) {
    Button(onClick = {
        onClick()
    },
        modifier = Modifier.fillMaxWidth(0.5f),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0077FF)
        )
    ) {
        Text(text = text)
    }
}
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
