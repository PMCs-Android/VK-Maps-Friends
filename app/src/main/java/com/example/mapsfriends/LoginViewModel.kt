package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    fun getRepository(): UserRepository {
        return userRepository
    }
    fun signUp(
        userId: String,
        username: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            userRepository.setUser(
                userId = userId,
                username = username,
                avatarUrl = "",
                friends = emptyList(),
                location = GeoPoint(0.0, 0.0)
            )
            onSuccess()
        }
    }

    fun signIn(
        userId: String,
        username: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            if (user == null) {
                userRepository.setUser(
                    userId = userId,
                    username = username,
                    avatarUrl = "",
                    friends = emptyList(),
                    location = GeoPoint(0.0, 0.0)
                )
            }
            onSuccess()
        }
    }
}
