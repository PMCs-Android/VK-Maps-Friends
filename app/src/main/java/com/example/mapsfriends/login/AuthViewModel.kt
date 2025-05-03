package com.example.mapsfriends.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenManager: AuthTokenManager
) : ViewModel() {

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
