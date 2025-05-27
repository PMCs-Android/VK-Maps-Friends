package com.example.mapsfriends.messenger

import android.util.Log
import com.example.mapsfriends.UserProfileRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseMessengerRepository @Inject constructor(
    private val userRepository: UserProfileRepository
) : MessengerRepository {
    private val chats = Firebase.firestore.collection("chats")
    private val database = Firebase.firestore

    override suspend fun getChatById(chatId: String): Chat? {
        return try {
            val snapshot = chats.document(chatId).get().await()
            snapshot.toObject<Chat>()
        } catch (e: Exception) {
            Log.e("MessengerRepository", "Error getting chat $chatId: ${e.message}", e)
            null
        }
    }

    override suspend fun createChat(userId1: String, userId2: String): String {
        try {
            val chatId = if (userId1 < userId2) "${userId1}_$userId2" else "${userId2}_$userId1"
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

    override suspend fun updateChatParticipants(chatId: String, participants: List<String>) {
        try {
            val validParticipants = coroutineScope {
                participants.map { userId ->
                    async { userRepository.getUserById(userId) }
                }.awaitAll().filterNotNull()
            }
            if (validParticipants.size != participants.size) {
                throw NoSuchElementException("One or more participants do not exist")
            }

            chats.document(chatId).update("participants", participants).await()

            coroutineScope {
                participants.map { userId ->
                    async {
                        database.collection("users")
                            .document(userId)
                            .update("chats", FieldValue.arrayUnion(chatId))
                            .await()
                    }
                }.awaitAll()
            }
            Log.d("MessengerRepository", "Updated participants for chat: $chatId")
        } catch (e: Exception) {
            Log.e("MessengerRepository", "Error updating chat participants: ${e.message}", e)
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
                        close(error)
                        return@addSnapshotListener
                    }
                    val messageList = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            val message = doc.toObject(Message::class.java)
                            message
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()
                    trySend(messageList)
                }
            awaitClose { listener.remove() }
        } catch (e: IOException) {
            Log.e(
                "MessengerRepository",
                "Network error setting up message observation: ${e.message}",
                e
            )
            close(e)
        } catch (e: FirebaseFirestoreException) {
            Log.e(
                "MessengerRepository",
                "Firestore error setting up message observation: ${e.code} - ${e.message}",
                e
            )
            close(e)
        }
    }

    override suspend fun createGroupChat(participants: List<String>, eventId: String): String {
        try {
            val chatId = "group_$eventId"
            val chat = Chat(
                chatId = chatId,
                participants = participants,
                isGroupChat = true,
                eventId = eventId
            )

            val chatSnapshot = chats.document(chatId).get().await()
            if (!chatSnapshot.exists()) {
                val validParticipants = coroutineScope {
                    participants.map { userId ->
                        async { userRepository.getUserById(userId) }
                    }.awaitAll().filterNotNull()
                }
                if (validParticipants.size != participants.size) {
                    throw NoSuchElementException("One or more participants do not exist")
                }

                chats.document(chatId).set(chat).await()

                coroutineScope {
                    participants.map { userId ->
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
            println("Network error while creating group chat: ${e.message}")
            throw e
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error while creating group chat: ${e.code} - ${e.message}")
            throw e
        }
    }

    override suspend fun addParticipantToChat(chatId: String, userId: String) {
        try {
            val user = userRepository.getUserById(userId)
            if (user == null) {
                throw NoSuchElementException("User $userId does not exist")
            }
            chats.document(chatId)
                .update("participants", FieldValue.arrayUnion(userId))
                .await()
            database.collection("users")
                .document(userId)
                .update("chats", FieldValue.arrayUnion(chatId))
                .await()
        } catch (e: IOException) {
            println("Network error while adding participant to chat: ${e.message}")
            throw e
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error while adding participant to chat: ${e.code} - ${e.message}")
            throw e
        }
    }

    override suspend fun removeParticipantFromChat(chatId: String, userId: String) {
        try {
            chats.document(chatId)
                .update("participants", FieldValue.arrayRemove(userId))
                .await()
            database.collection("users")
                .document(userId)
                .update("chats", FieldValue.arrayRemove(chatId))
                .await()
        } catch (e: IOException) {
            println("Network error while removing participant from chat: ${e.message}")
            throw e
        } catch (e: FirebaseFirestoreException) {
            println(
                "Firestore error while removing participant from chat: ${e.code} - ${e.message}"
            )
            throw e
        }
    }

    override suspend fun deleteChat(chatId: String) {
        try {
            val chatSnapshot = chats.document(chatId).get().await()
            if (chatSnapshot.exists()) {
                val participants = chatSnapshot.get("participants") as? List<String> ?: emptyList()
                coroutineScope {
                    participants.map { userId ->
                        async {
                            database.collection("users")
                                .document(userId)
                                .update("chats", FieldValue.arrayRemove(chatId))
                                .await()
                        }
                    }.awaitAll()
                }
                chats.document(chatId).delete().await()
            }
        } catch (e: IOException) {
            println("Network error while deleting chat: ${e.message}")
            throw e
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error while deleting chat: ${e.code} - ${e.message}")
            throw e
        }
    }
}
