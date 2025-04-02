package com.example.mapsfriends.ui.login

<<<<<<< HEAD
=======
import android.util.Log
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
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
<<<<<<< HEAD
import com.example.mapsfriends.R
import com.example.mapsfriends.ui.profile.data.ProfileScreenDataObject
import com.example.mapsfriends.ui.theme.MainGradient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(onNavigateToProfileScreen: (ProfileScreenDataObject) -> Unit) {
    val auth =
        remember {
            Firebase.auth
        }

    val errorState =
        remember {
            mutableStateOf("")
        }

    val emailState =
        remember {
            mutableStateOf("")
        }
    val passwordState =
        remember {
            mutableStateOf("")
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MainGradient)
                .padding(
                    start = 40.dp,
                    end = 40.dp,
                ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.mapsfriends),
            contentDescription = "Logo",
=======
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.example.mapsfriends.R
import com.example.mapsfriends.ui.profile.data.ProfileScreenDataObject
import com.example.mapsfriends.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToProfileScreen: (ProfileScreenDataObject) -> Unit
) {
    val auth = remember {
        Firebase.auth
    }

    val errorState = remember {
        mutableStateOf("")
    }

    val emailState = remember {
        mutableStateOf("")
    }
    val passwordState = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .padding(
                start = 40.dp, end = 40.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.mapsfriends),
            contentDescription = "Logo"
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
        )
        RoundedCornerTextField(
            text = emailState.value,
            label = "Email",
<<<<<<< HEAD
=======

>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
        ) {
            emailState.value = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        RoundedCornerTextField(
            text = passwordState.value,
            label = "Password",
<<<<<<< HEAD
        ) {
=======

            ) {
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
            passwordState.value = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (errorState.value.isNotEmpty()) {
            Text(
                text = errorState.value,
                color = Color.Red,
<<<<<<< HEAD
                textAlign = TextAlign.Center,
=======
                textAlign = TextAlign.Center
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
            )
        }
        VKIDButton()
        LoginButton("Sign In") {
            signIn(
<<<<<<< HEAD
                auth,
                emailState.value,
                passwordState.value,
=======
                auth, emailState.value, passwordState.value,
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
                onSignInSuccess = { navData ->
                    onNavigateToProfileScreen(navData)
                },
                onSignInFailure = { error ->
                    errorState.value = error
<<<<<<< HEAD
                },
=======
                }
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
            )
        }
        LoginButton("Sign Up") {
            signUp(
<<<<<<< HEAD
                auth,
                emailState.value,
                passwordState.value,
=======
                auth, emailState.value, passwordState.value,
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
                onSignUpSuccess = { navData ->
                    onNavigateToProfileScreen(navData)
                },
                onSignUpFailure = { error ->
                    errorState.value = error
<<<<<<< HEAD
                },
=======
                }
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
            )
        }
    }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignUpSuccess: (ProfileScreenDataObject) -> Unit,
<<<<<<< HEAD
    onSignUpFailure: (String) -> Unit,
=======
    onSignUpFailure: (String) -> Unit
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
) {
    if (email.isBlank() || password.isBlank()) {
        onSignUpFailure("Email and password cannot be empty")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                onSignUpSuccess(
                    ProfileScreenDataObject(
                        it.result.user?.uid!!,
<<<<<<< HEAD
                        it.result.user?.email!!,
                    ),
=======
                        it.result.user?.email!!
                    )
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
                )
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
    onSignInSuccess: (ProfileScreenDataObject) -> Unit,
<<<<<<< HEAD
    onSignInFailure: (String) -> Unit,
=======
    onSignInFailure: (String) -> Unit
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
) {
    if (email.isBlank() || password.isBlank()) {
        onSignInFailure("Email and password cannot be empty")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                onSignInSuccess(
                    ProfileScreenDataObject(
                        it.result.user?.uid!!,
<<<<<<< HEAD
                        it.result.user?.email!!,
                    ),
=======
                        it.result.user?.email!!
                    )
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
                )
            }
        }
        .addOnFailureListener {
            onSignInFailure(it.message ?: "Sign In failure")
        }
}

<<<<<<< HEAD
// private fun signOut(auth: FirebaseAuth) {
//    auth.signOut()
// }
//
// private fun deleteAccount(auth: FirebaseAuth, email: String, password: String) {
=======
//private fun signOut(auth: FirebaseAuth) {
//    auth.signOut()
//}
//
//private fun deleteAccount(auth: FirebaseAuth, email: String, password: String) {
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
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
<<<<<<< HEAD
// }
=======
//}
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
