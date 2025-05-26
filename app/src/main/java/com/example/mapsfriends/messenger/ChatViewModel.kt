package com.example.mapsfriends.messenger

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.Event
import com.example.mapsfriends.EventRepository
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
class ChatViewModel @Inject constructor(
    private val messengerRepository: MessengerRepository,
    private val userRepository: UserProfileRepository,
    private val eventRepository: EventRepository,
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val _currentChat = MutableStateFlow<Chat?>(null)
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    private val _otherUser = MutableStateFlow<User?>(null)
    private val _event = MutableStateFlow<Event?>(null)
    private val _users = MutableStateFlow<Map<String, User>>(emptyMap())

    val currentUserId: String = tokenManager.getUserId() ?: ""
    val currentChat: StateFlow<Chat?> = _currentChat
    val messages: StateFlow<List<Message>> = _messages
    val otherUser: StateFlow<User?> = _otherUser
    val event: StateFlow<Event?> = _event
    val users: StateFlow<Map<String, User>> = _users

    fun loadChat(chatId: String) {
        viewModelScope.launch {
            try {
                launch {
                    messengerRepository.observeChats(currentUserId)
                        .catch { e ->
                            when (e) {
                                is FirebaseFirestoreException -> {
                                    Log.e(TAG, "Firestore error loading chat: ${e.message}", e)
                                }
                                is IOException -> {
                                    Log.e(TAG, "Network error loading chat: ${e.message}", e)
                                }
                                else -> {
                                    Log.e(TAG, "Unknown error loading chat: ${e.message}", e)
                                }
                            }
                            _currentChat.value = null
                            _otherUser.value = null
                            _event.value = null
                        }
                        .collect { chats ->
                            val chat = chats.find { it.chatId == chatId }
                            _currentChat.value = chat
                            if (chat != null) {
                                if (!chat.isGroupChat) {
                                    val otherUserId = chat.participants.firstOrNull { it != currentUserId }
                                    if (otherUserId != null) {
                                        val user = userRepository.getUserById(otherUserId)
                                        _otherUser.value = user
                                        Log.d(TAG, "Loaded other user: ${user?.username}")
                                    } else {
                                        _otherUser.value = null
                                    }
                                }
                                // Для группового чата загружаем событие
                                if (chat.isGroupChat && chat.eventId != null) {
                                    val event = eventRepository.getEventById(chat.eventId)
                                    _event.value = event
                                    Log.d(TAG, "Loaded event: ${event?.title}")
                                }
                            } else {
                                _otherUser.value = null
                                _event.value = null
                            }
                        }
                }

                launch {
                    messengerRepository.observeMessages(chatId)
                        .catch { e ->
                            when (e) {
                                is FirebaseFirestoreException -> {
                                    Log.e(TAG, "Firestore error loading messages: ${e.message}", e)
                                }
                                is IOException -> {
                                    Log.e(TAG, "Network error loading messages: ${e.message}", e)
                                }
                                else -> {
                                    Log.e(TAG, "Unknown error loading messages: ${e.message}", e)
                                }
                            }
                            _messages.value = emptyList()
                        }
                        .collect { messageList ->
                            val sortedMessages = messageList.sortedBy { it.timestamp.toDate() }
                            _messages.value = sortedMessages
                            updateUsersForMessages(sortedMessages)
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error loading chat data: ${e.message}", e)
            }
        }
    }

    private suspend fun updateUsersForMessages(messages: List<Message>) {
        try {
            val userIds = messages.map { it.senderId }.distinct()
            Log.d(TAG, "Loading users for messages: $userIds")
            coroutineScope {
                val userList = userIds.mapNotNull { userId ->
                    async { userRepository.getUserById(userId) }
                }.awaitAll()
                _users.value = userList.associateBy { it?.userId ?: "" }
                    .filterValues { it != null } as Map<String, User>
                Log.d(TAG, "Users loaded: ${_users.value.keys}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading users for messages: ${e.message}", e)
            _users.value = emptyMap()
        }
    }

    fun sendMessage(chatId: String, text: String) {
        viewModelScope.launch {
            try {
                if (text.isNotBlank()) {
                    messengerRepository.sendMessage(chatId, currentUserId, text)
                }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "Firestore error sending message: ${e.message}", e)
            } catch (e: IOException) {
                Log.e(TAG, "Network error sending message: ${e.message}", e)
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error sending message: ${e.message}", e)
            }
        }
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }
}