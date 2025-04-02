package com.example.mapsfriends.ui.login

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
<<<<<<< HEAD
=======
import kotlin.math.sin
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671

@Composable
fun RoundedCornerTextField(
    text: String,
    label: String,
<<<<<<< HEAD
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = text,
        onValueChange = {
            onValueChange(it)
        },
        shape = RoundedCornerShape(25.dp),
        colors =
            TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
        modifier =
            Modifier.fillMaxWidth()
                .border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
        label = {
            Text(text = label, color = Color.Gray)
        },
        singleLine = true,
    )
}
=======
    onValueChange: (String) -> Unit
) {
    TextField(value = text, onValueChange = {
        onValueChange(it)
    },
        shape = RoundedCornerShape(25.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
            .border(1.dp, Color.Black, RoundedCornerShape(25.dp)),
        label = {
            Text(text = label, color = Color.Gray)
        },
        singleLine = true
    )
}
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
