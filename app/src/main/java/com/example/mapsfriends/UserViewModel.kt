package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> = _friends.asStateFlow()
    private val _avatars = MutableStateFlow<Map<String, String>>(emptyMap())
    val avatars: StateFlow<Map<String, String>> = _avatars

    fun loadFriends(userId: String) {
        viewModelScope.launch {
            try {
                val friendsList = userRepository.getFriendsList(userId) ?: emptyList()
                _friends.value = friendsList
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading friends: ${e.message}")
                _friends.value = emptyList()
            } catch (e: IOException) {
                println("Network error loading friends: ${e.message}")
                _friends.value = emptyList()
            } catch (e: IllegalStateException) {
                println("Data error loading friends: ${e.message}")
                _friends.value = emptyList()
            }
        }
    }

    fun loadAvatars(userIds: List<String>) {
        viewModelScope.launch {
            try {
                val result = userRepository.getUserAvatars(userIds)
                _avatars.value = result.filterValues { it != null } as Map<String, String>
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading avatars: ${e.message}")
                _avatars.value = emptyMap()
            } catch (e: IOException) {
                println("Network error loading avatars: ${e.message}")
                _avatars.value = emptyMap()
            }
        }
    }
}
