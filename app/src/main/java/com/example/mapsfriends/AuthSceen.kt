package com.example.mapsfriends

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vk.id.onetap.compose.onetap.OneTap
import com.vk.id.onetap.compose.onetap.OneTapTitleScenario

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    OneTap(
        onAuth = { _, token ->
            viewModel.getUserData(token.token) { user ->
                //Log.d("VK_USER", "ID: ${user.id}, Email: ${user.email}")
            }
        },
        onFail = { _, fail ->
            Toast.makeText(context, "Error: ${fail.description}", Toast.LENGTH_LONG).show()
        },
        scenario = OneTapTitleScenario.SignIn
    )
}