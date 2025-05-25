package com.example.mapsfriends.messenger

import com.example.mapsfriends.UserProfileRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import okio.IOException
import java.util.UUID
import javax.inject.Inject

class FirebaseMessengerRepository @Inject constructor(
    private val userRepository: UserProfileRepository
) : MessengerRepository {
    private val chats = Firebase.firestore.collection("chats")
    private val database = Firebase.firestore

    override suspend fun createChat(userId1: String, userId2: String): String {
        try {
            val chatId = if (userId1 < userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
            val chat = Chat(
                chatId = chatId,
                participants = listOf(userId1, userId2),
                lastMessage = "",
                lastMessageTime = Timestamp.now()
            )

            val chatSnapshot = chats.document(chatId).get().await()
            if (!chatSnapshot.exists()) {
                val user1 = userRepository.getUserById(userId1)
                val user2 = userRepository.getUserById(userId2)
                if (user1 == null || user2 == null) {
                    throw NoSuchElementException("User $userId1 or $userId2 does not exist")
                }

                chats.document(chatId).set(chat).await()

                coroutineScope {
                    listOf(userId1, userId2).map { userId ->
                        async {
                            database.collection("users")
                                .document(userId)
                                .update("chats", FieldValue.arrayUnion(chatId))
                                .await()
                        }
                    }.awaitAll()
                }
            }
            return chatId
        } catch (e: IOException) {
            println("Network error while creating chat: ${e.message}")
            throw e
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error while creating chat: ${e.code} - ${e.message}")
            throw e
        } catch (e: IllegalStateException) {
            println("Data conversion error while creating chat: ${e.message}")
            throw e
        }
    }

    override suspend fun sendMessage(chatId: String, senderId: String, text: String) {
        try {
            val chatSnapshot = chats.document(chatId).get().await()
            if (!chatSnapshot.exists()) {
                throw NoSuchElementException("Chat $chatId does not exist")
            }

            val message = Message(
                messageId = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = senderId,
                text = text,
                timestamp = Timestamp.now(),
                isRead = false
            )

            chats.document(chatId)
                .collection("messages")
                .document(message.messageId)
                .set(message)
                .await()

            chats.document(chatId)
                .update(
                    mapOf(
                        "lastMessage" to text,
                        "lastMessageTime" to message.timestamp
                    )
                )
                .await()
        } catch (e: IOException) {
            println("Network error while sending message: ${e.message}")
            throw e
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error while sending message: ${e.code} - ${e.message}")
            throw e
        } catch (e: IllegalStateException) {
            println("Data conversion error while sending message: ${e.message}")
            throw e
        }
    }

    override fun observeChats(userId: String): Flow<List<Chat>> = callbackFlow {
        try {
            val listener = chats
                .whereArrayContains("participants", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        println("Firestore error while observing chats: ${error.message}")
                        close(error)
                        return@addSnapshotListener
                    }
                    val chatList = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(Chat::class.java)
                        } catch (e: IllegalStateException) {
                            println("Data conversion error for chat ${doc.id}: ${e.message}")
                            null
                        }
                    } ?: emptyList()
                    trySend(chatList)
                }
            awaitClose { listener.remove() }
        } catch (e: IOException) {
            println("Network error while setting up chat observation: ${e.message}")
            close(e)
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error while setting up chat observation: ${e.code} - ${e.message}")
            close(e)
        }
    }

    override fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        try {
            val listener = chats.document(chatId)
                .collection("messages")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        println("Firestore error while observing messages: ${error.message}")
                        close(error)
                        return@addSnapshotListener
                    }
                    val messageList = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(Message::class.java)
                        } catch (e: IllegalStateException) {
                            println("Data conversion error for message ${doc.id}: ${e.message}")
                            null
                        }
                    } ?: emptyList()
                    trySend(messageList)
                }
            awaitClose { listener.remove() }
        } catch (e: IOException) {
            println("Network error while setting up message observation: ${e.message}")
            close(e)
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error while setting up message observation: ${e.code} - ${e.message}")
            close(e)
        }
    }
}