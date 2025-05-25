package com.example.mapsfriends.messenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.User
import com.example.mapsfriends.UserProfileRepository
import com.example.mapsfriends.login.AuthTokenManager
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class ChatsListViewModel @Inject constructor(
    private val messengerRepository: MessengerRepository,
    private val userRepository: UserProfileRepository,
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    private val _users = MutableStateFlow<Map<String, User>>(emptyMap())

    val currentUserId: String = tokenManager.getUserId() ?: ""

    val chats: StateFlow<List<Chat>> = _chats
    val users: StateFlow<Map<String, User>> = _users

    init {
        loadChatsForUser(currentUserId)
    }

    fun loadChatsForUser(userId: String) {
        viewModelScope.launch {
            messengerRepository.observeChats(userId)
                .catch { e ->
                    when (e) {
                        is FirebaseFirestoreException -> {
                            println("Firestore error loading chats: ${e.message}")
                        }
                        is IOException -> {
                            println("Network error loading chats: ${e.message}")
                        }
                        else -> {
                            println("Unknown error loading chats: ${e.message}")
                        }
                    }
                    _chats.value = emptyList()
                }
                .collect { chatList ->
                    val sortedChats = chatList.sortedByDescending {
                        it.lastMessageTime.toDate()
                    }
                    _chats.value = sortedChats
                    updateUsersForChats(sortedChats)
                }
        }
    }

    private suspend fun updateUsersForChats(chats: List<Chat>) {
        try {
            val userIds = chats.flatMap { it.participants }.distinct()
            coroutineScope {
                val userList = userIds.mapNotNull { userId ->
                    async { userRepository.getUserById(userId) }
                }.awaitAll()
                _users.value = userList.associateBy { it?.userId ?: "" }
                    .filterValues { it != null } as Map<String, User>
            }
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error fetching users: ${e.message}")
            _users.value = emptyMap()
        } catch (e: IOException) {
            println("Network error fetching users: ${e.message}")
            _users.value = emptyMap()
        } catch (e: Exception) {
            println("Unexpected error fetching users: ${e.message}")
            _users.value = emptyMap()
        }
    }
}