package com.example.mapsfriends.login.firebase

import androidx.lifecycle.ViewModel
import com.example.mapsfriends.User
import com.example.mapsfriends.UserProfileRepository
import com.example.mapsfriends.login.AuthTokenManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class FirebaseAuthViewModel @Inject constructor(
    private val tokenManager: AuthTokenManager,
    private val repository: UserProfileRepository
) : ViewModel() {
    private val auth = Firebase.auth

    fun signUp(
        email: String,
        password: String,
        onSignUpSuccess: () -> Unit,
        onSignUpFailure: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onSignUpFailure("Email and password cannot be empty")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val userId = it.result.user?.uid!!

                    CoroutineScope(Dispatchers.IO).launch {
                        saveUserInFirebase(
                            token = "",
                            User(
                                userId = userId,
                                username = email,
                                avatarUrl = "",
                                friends = emptyList(),
                                location = GeoPoint(0.0, 0.0)
                            )
                        )
                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                            onSignUpSuccess()
                        }
                    }
                }
            }
            .addOnFailureListener {
                onSignUpFailure(it.message ?: "Sign Up failure")
            }
    }

    fun signIn(
        email: String,
        password: String,
        onSignInSuccess: () -> Unit,
        onSignInFailure: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onSignInFailure("Email and password cannot be empty")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val userId = it.result.user?.uid!!

                    CoroutineScope(Dispatchers.IO).launch {
                        val user = repository.getUserById(userId)
                        if (user == null) {
                            saveUserInFirebase(
                                token = "",
                                User(
                                    userId = userId,
                                    username = email,
                                    avatarUrl = "",
                                    friends = emptyList(),
                                    location = GeoPoint(0.0, 0.0)
                                )
                            )
                        }
                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                            onSignInSuccess()
                        }
                    }
                }
            }
            .addOnFailureListener {
                onSignInFailure(it.message ?: "Sign In failure")
            }
    }

    fun signOut() {
        auth.signOut()
    }

    private suspend fun saveUserInFirebase(
        token: String,
        user: User
    ) {
        tokenManager.saveAuthData(token = token, userId = user.userId)

        repository.setUser(
            userId = user.userId,
            username = user.username,
            avatarUrl = user.avatarUrl,
            friends = user.friends,
            location = user.location
        )
    }
}
