package com.example.mapsfriends.messenger

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
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
    @PropertyName("chatId") val chatId: String = "",
    @PropertyName("participants") val participants: List<String> = emptyList(),
    @PropertyName("lastMessage") val lastMessage: String = "",
    @PropertyName("lastMessageTime") val lastMessageTime: Timestamp = Timestamp.now(),
    @PropertyName("groupChat") val isGroupChat: Boolean = false,
    @PropertyName("eventId") val eventId: String? = null
)

interface MessengerRepository {
    suspend fun getChatById(chatId: String): Chat?
    suspend fun createChat(userId1: String, userId2: String): String
    suspend fun updateChatParticipants(chatId: String, participants: List<String>)
    suspend fun sendMessage(chatId: String, senderId: String, text: String)
    fun observeChats(userId: String): Flow<List<Chat>>
    fun observeMessages(chatId: String): Flow<List<Message>>
    suspend fun createGroupChat(participants: List<String>, eventId: String): String
    suspend fun addParticipantToChat(chatId: String, userId: String)
    suspend fun removeParticipantFromChat(chatId: String, userId: String)
    suspend fun deleteChat(chatId: String)
}

