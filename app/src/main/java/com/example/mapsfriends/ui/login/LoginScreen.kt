package com.example.mapsfriends.ui.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mapsfriends.R
import com.example.mapsfriends.login.firebase.FirebaseAuthViewModel
import com.example.mapsfriends.ui.theme.MainGradient

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    firebaseAuthViewModel: FirebaseAuthViewModel = hiltViewModel()
) {
    val errorState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    
    // Обработка ошибок при входе
    LaunchedEffect(Unit) {
        try {
            Log.d("LoginScreen", "Инициализация экрана входа")
        } catch (e: Exception) {
            Log.e("LoginScreen", "Ошибка при инициализации экрана входа", e)
            errorState.value = "Ошибка при инициализации экрана входа"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .padding(start = 40.dp, end = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.mapsfriends),
            contentDescription = "Logo"
        )
        RoundedCornerTextField(
            text = emailState.value,
            label = "Email",
        ) {
            emailState.value = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        RoundedCornerTextField(
            text = passwordState.value,
            label = "Password",
        ) {
            passwordState.value = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (errorState.value.isNotEmpty()) {
            Text(
                text = errorState.value,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }
        VKIDButton(
            onLoginSuccess = {
                try {
                    onLoginSuccess()
                } catch (e: Exception) {
                    Log.e("LoginScreen", "Ошибка при входе через VK", e)
                    errorState.value = "Ошибка при входе через VK: ${e.message}"
                }
            }
        )
        LoginButton("Sign In") {
            try {
                firebaseAuthViewModel.signIn(
                    emailState.value,
                    passwordState.value,
                    onSignInSuccess = onLoginSuccess,
                    onSignInFailure = { error ->
                        errorState.value = error
                    }
                )
            } catch (e: Exception) {
                Log.e("LoginScreen", "Ошибка при входе", e)
                errorState.value = "Ошибка при входе: ${e.message}"
            }
        }
        LoginButton("Sign Up") {
            try {
                firebaseAuthViewModel.signUp(
                    emailState.value,
                    passwordState.value,
                    onSignUpSuccess = onLoginSuccess,
                    onSignUpFailure = { error ->
                        errorState.value = error
                    }
                )
            } catch (e: Exception) {
                Log.e("LoginScreen", "Ошибка при регистрации", e)
                errorState.value = "Ошибка при регистрации: ${e.message}"
            }
        }
    }
}
