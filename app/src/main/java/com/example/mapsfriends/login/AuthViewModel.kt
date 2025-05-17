package com.example.mapsfriends.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenManager: AuthTokenManager,
    private val repository: UserProfileRepository
) : ViewModel() {
    private val _isUserRegistered = MutableStateFlow<Boolean?>(null)
    val isUserRegistered: StateFlow<Boolean?> = _isUserRegistered

    fun getCurrentUserId(): String? {
        return tokenManager.getUserId()
    }

    fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }

    fun saveAuthData(token: String, userId: String) {
        tokenManager.saveAuthData(token, userId)
    }

    fun checkUserInFirebase() {
        val userId = getCurrentUserId()
        if (userId == null) {
            _isUserRegistered.value = false
            return
        }

        viewModelScope.launch {
            try {
                val userProfile = repository.getUserById(userId)
                _isUserRegistered.value = (userProfile != null)
            } catch (e: Exception) {
                _isUserRegistered.value = false
            }
        }
    }

    fun logout() {
        tokenManager.clear()
    }
}
