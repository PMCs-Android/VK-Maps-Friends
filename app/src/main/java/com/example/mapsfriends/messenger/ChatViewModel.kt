package com.example.mapsfriends.messenger

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.User
import com.example.mapsfriends.UserProfileRepository
import com.example.mapsfriends.login.AuthTokenManager
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val _currentChat = MutableStateFlow<Chat?>(null)
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    private val _otherUser = MutableStateFlow<User?>(null)

    val currentUserId: String = tokenManager.getUserId() ?: ""

    val currentChat: StateFlow<Chat?> = _currentChat
    val messages: StateFlow<List<Message>> = _messages
    val otherUser: StateFlow<User?> = _otherUser

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
                        }
                        .collect { chats ->
                            val chat = chats.find { it.chatId == chatId }
                            _currentChat.value = chat
                            if (chat != null) {
                                val otherUserId = chat.participants.firstOrNull { it != currentUserId }
                                if (otherUserId != null) {
                                    val user = userRepository.getUserById(otherUserId)
                                    _otherUser.value = user
                                } else {
                                    _otherUser.value = null
                                }
                            } else {
                                _otherUser.value = null
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
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error loading chat data: ${e.message}", e)
            }
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