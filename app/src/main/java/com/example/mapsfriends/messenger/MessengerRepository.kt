package com.example.mapsfriends.messenger

import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow

data class Message(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val isRead: Boolean = false
)

data class Chat(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Timestamp = Timestamp.now()
)

interface MessengerRepository {
    suspend fun createChat(userId1: String, userId2: String): String
    suspend fun sendMessage(chatId: String, senderId: String, text: String)
    fun observeChats(userId: String): Flow<List<Chat>>
    fun observeMessages(chatId: String): Flow<List<Message>>
}

