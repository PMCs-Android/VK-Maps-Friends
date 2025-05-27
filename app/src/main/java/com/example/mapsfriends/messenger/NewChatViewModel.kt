package com.example.mapsfriends.messenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.User
import com.example.mapsfriends.UserProfileRepository
import com.example.mapsfriends.login.AuthTokenManager
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException

@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val messengerRepository: MessengerRepository,
    private val userRepository: UserProfileRepository,
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val _friends = MutableStateFlow<List<User>>(emptyList())

    val currentUserId: String = tokenManager.getUserId() ?: ""

    val friends: StateFlow<List<User>> = _friends

    init {
        loadFriendsForUser(currentUserId)
    }

    fun loadFriendsForUser(userId: String) {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getUserById(userId)
                val friendIds = currentUser?.friends ?: emptyList()
                coroutineScope {
                    val friendList = friendIds.mapNotNull { friendId ->
                        async { userRepository.getUserById(friendId) }
                    }.awaitAll().filterNotNull()
                    _friends.value = friendList.sortedBy { it.username }
                }
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading friends: ${e.message}")
                _friends.value = emptyList()
            } catch (e: IOException) {
                println("Network error loading friends: ${e.message}")
                _friends.value = emptyList()
            } catch (e: Exception) {
                println("Unexpected error loading friends: ${e.message}")
                _friends.value = emptyList()
            }
        }
    }

    fun createChat(friendId: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val chatId = messengerRepository.createChat(currentUserId, friendId)
                onSuccess(chatId)
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error creating chat: ${e.message}")
            } catch (e: IOException) {
                println("Network error creating chat: ${e.message}")
            } catch (e: Exception) {
                println("Unexpected error creating chat: ${e.message}")
            }
        }
    }
}
