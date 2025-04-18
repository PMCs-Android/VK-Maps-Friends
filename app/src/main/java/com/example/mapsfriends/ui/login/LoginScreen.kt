package com.example.mapsfriends.ui.login

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsfriends.AuthScreen
import com.example.mapsfriends.AuthTokenManager
import com.example.mapsfriends.FirebaseUserRepository
import com.example.mapsfriends.R
import com.example.mapsfriends.ui.theme.MainGradient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    tokenManager: AuthTokenManager,
    onLoginSuccess: () -> Unit
) {
    val auth = remember { Firebase.auth }
    val errorState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
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
            tokenManager = tokenManager, // Передаем менеджер в кнопку
            onLoginSuccess = onLoginSuccess
        )
        LoginButton("Sign In") {
            signIn(
                auth,
                emailState.value,
                passwordState.value,
                onSignInSuccess = {
                    //navController.navigate("main")
                },
                onSignInFailure = { error ->
                    errorState.value = error
                },
            )
        }
        LoginButton("Sign Up") {
            signUp(
                auth,
                emailState.value,
                passwordState.value,
                onSignUpSuccess = {
                    //navController.navigate("main")
                },
                onSignUpFailure = { error ->
                    errorState.value = error
                },
            )
        }
    }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignUpSuccess: (String) -> Unit,
    onSignUpFailure: (String) -> Unit,
) {
    if (email.isBlank() || password.isBlank()) {
        onSignUpFailure("Email and password cannot be empty")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = it.result.user?.uid!!
                val repository = FirebaseUserRepository()

                CoroutineScope(Dispatchers.IO).launch {
                    repository.setUser(
                        userId = userId,
                        username = email,
                        avatarUrl = "",
                        friends = emptyList(),
                        location = GeoPoint(0.0, 0.0)
                    )
                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                        onSignUpSuccess(userId)
                    }
                }
            }
        }
        .addOnFailureListener {
            onSignUpFailure(it.message ?: "Sign Up failure")
        }
}

private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignInSuccess: (String) -> Unit,
    onSignInFailure: (String) -> Unit,
) {
    if (email.isBlank() || password.isBlank()) {
        onSignInFailure("Email and password cannot be empty")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = it.result.user?.uid!!
                val repository = FirebaseUserRepository()

                CoroutineScope(Dispatchers.IO).launch {
                    val user = repository.getUserById(userId)
                    if (user == null) {
                        repository.setUser(
                            userId = userId,
                            username = email,
                            avatarUrl = "",
                            friends = emptyList(),
                            location = GeoPoint(0.0, 0.0)
                        )
                    }
                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                        onSignInSuccess(userId)
                    }
                }
            }
        }
        .addOnFailureListener {
            onSignInFailure(it.message ?: "Sign In failure")
        }
}

// private fun signOut(auth: FirebaseAuth) {
//    auth.signOut()
// }
//
//
// private fun deleteAccount(auth: FirebaseAuth, email: String, password: String) {
//    val credential = EmailAuthProvider.getCredential(email, password)
//    auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
//        if (it.isSuccessful) {
//            auth.currentUser?.delete()?.addOnCompleteListener {
//                if (it.isSuccessful) {
//                    Log.d("MyLog", "Account deleted")
//                } else {
//                    Log.d("MyLog", "Account not deleted")
//                }
//            }
//        } else {
//            Log.d("MyLog", "Failure auth")
//        }
//    }
