package com.example.mapsfriends.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.User
import com.example.mapsfriends.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenManager: AuthTokenManager,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    private val _isUserRegistered = MutableStateFlow<Boolean?>(null)
    val isUserRegistered: StateFlow<Boolean?> = _isUserRegistered

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun getUser(userId: String) {
        viewModelScope.launch {
            _currentUser.value = userProfileRepository.getUserById(userId)
        }
    }
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

    fun checkUserInFirebase() {
        val userId = getCurrentUserId()
        if (userId == null) {
            _isUserRegistered.value = false
            return
        }

        viewModelScope.launch {
            try {
                val userProfile = userProfileRepository.getUserById(userId)
                _isUserRegistered.value = (userProfile != null)
            } catch (e: IOException) {
                _isUserRegistered.value = false
            }
        }
    }

    fun logout() {
        tokenManager.clear()
    }
}
