package com.example.mapsfriends.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val _currentUserId = MutableStateFlow<String?>(getCurrentUserId())
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    fun getCurrentUserId(): String? {
        return tokenManager.getUserId()
    }

    fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }

    fun saveAuthData(token: String, userId: String) {
        tokenManager.saveAuthData(token, userId)
    }
}
