package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.login.AuthTokenManager
import com.example.mapsfriends.messenger.Chat
import com.example.mapsfriends.messenger.MessengerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val messengerRepository: MessengerRepository,
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> = _friends

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val currentUserId: String = tokenManager.getUserId() ?: ""

    init {
        startObservingChats()
    }

    private fun startObservingChats() {
        viewModelScope.launch {
            messengerRepository.observeChats(currentUserId)
                .catch { e ->
                }
                .collectLatest { chatsList ->
                    _chats.value = chatsList
                }
        }
    }

    suspend fun getOrCreateChat(friendId: String): String {
        val existingChat = _chats.value.find { chat ->
            chat.participants.contains(currentUserId) && chat.participants.contains(friendId)
        }
        return if (existingChat != null) {
            existingChat.chatId
        } else {
            messengerRepository.createChat(currentUserId, friendId)
        }
    }
}
