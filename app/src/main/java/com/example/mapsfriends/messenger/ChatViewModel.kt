package com.example.mapsfriends.messenger

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
                messengerRepository.observeChats(currentUserId)
                    .catch { e ->
                        when (e) {
                            is FirebaseFirestoreException -> {
                                println("Firestore error loading chat: ${e.message}")
                            }
                            is IOException -> {
                                println("Network error loading chat: ${e.message}")
                            }
                            else -> {
                                println("Unknown error loading chat: ${e.message}")
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

                messengerRepository.observeMessages(chatId)
                    .catch { e ->
                        when (e) {
                            is FirebaseFirestoreException -> {
                                println("Firestore error loading messages: ${e.message}")
                            }
                            is IOException -> {
                                println("Network error loading messages: ${e.message}")
                            }
                            else -> {
                                println("Unknown error loading messages: ${e.message}")
                            }
                        }
                        _messages.value = emptyList()
                    }
                    .collect { messageList ->
                        val sortedMessages = messageList.sortedBy { it.timestamp.toDate() }
                        _messages.value = sortedMessages
                    }
            } catch (e: Exception) {
                println("Unexpected error loading chat data: ${e.message}")
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
                println("Firestore error sending message: ${e.message}")
            } catch (e: IOException) {
                println("Network error sending message: ${e.message}")
            } catch (e: Exception) {
                println("Unexpected error sending message: ${e.message}")
            }
        }
    }
}