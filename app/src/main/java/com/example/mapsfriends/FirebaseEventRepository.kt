package com.example.mapsfriends

import com.example.mapsfriends.messenger.MessengerRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseEventRepository @Inject constructor(
    private val userRepository: UserProfileRepository,
    private val messengerRepository: MessengerRepository
) : EventRepository {
    private val events = Firebase.firestore.collection("events")
    private val database = Firebase.firestore

    override suspend fun createEvent(event: Event) {
        events.document(event.eventId)
            .set(event)
            .await()

        messengerRepository.createGroupChat(event.participants, event.eventId)

        database.collection("users")
            .document(event.creatorId)
            .update("events", FieldValue.arrayUnion(event.eventId))
            .await()

        coroutineScope {
            event.invites.map { inviteId ->
                launch { sendInvite(event.eventId, inviteId) }
            }.joinAll()
        }
    }

    override suspend fun sendInvite(eventId: String, userId: String) {
        val user = database
            .collection("users")
            .document(userId)
            .get()
            .await()
        val event = events
            .document(eventId)
            .get()
            .await()

        if (user.exists() && event.exists()) {
            val participants = event.getStringList("participants")

            if (participants.contains(userId)) return

            database
                .collection("users")
                .document(userId)
                .update("invites", FieldValue.arrayUnion(eventId))

            events
                .document(eventId)
                .update("invites", FieldValue.arrayUnion(userId))
        }
    }

    override suspend fun addParticipant(eventId: String, userId: String) {
        try {
            val userRef = Firebase.firestore.collection("users").document(userId)
            if (!userRef.get().await().exists()) {
                return
            }
            events.document(eventId)
                .update("participants", FieldValue.arrayUnion(userId))
                .await()

            userRef.update("events", FieldValue.arrayUnion(eventId))

            val chatId = "group_$eventId"
            messengerRepository.addParticipantToChat(chatId, userId)
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error while adding participant: ${e.message}")
        } catch (e: IOException) {
            println("Network error while adding participant: ${e.message}")
        }
    }

    override suspend fun getEventById(eventId: String): Event? {
        return try {
            val doc = events.document(eventId).get().await()

            if (doc.exists()) {
                val event = doc.toObject(Event::class.java)
                event
            } else {
                null
            }
        } catch (e: IOException) {
            println("Network error: $e")
            null
        } catch (e: IllegalStateException) {
            println("Data conversion error: $e")
            null
        } catch (e: FirebaseFirestoreException) {
            println("Firestore operation failed: ${e.code} - ${e.message}")
            null
        }
    }

    override suspend fun deleteEvent(eventId: String) {
        try {
            val event = events.document(eventId).get().await()
            val inviteIds = event.getStringList("invites")
            val participantIds = event.getStringList("participants")

            if (!event.exists()) {
                throw NoSuchElementException("Event with ID $eventId doesn't exist")
            }

            if (inviteIds.isNotEmpty()) {
                coroutineScope {
                    inviteIds.map { inviteId ->
                        async {
                            database.collection("users")
                                .document(inviteId)
                                .update("invites", FieldValue.arrayRemove(eventId))
                        }
                    }.awaitAll()
                }
            }
            if (participantIds.isNotEmpty()) {
                coroutineScope {
                    participantIds.map { participantId ->
                        async {
                            database.collection("users")
                                .document(participantId)
                                .update("events", FieldValue.arrayRemove(eventId))
                        }
                    }.awaitAll()
                }
            }

            val chatId = "group_$eventId"
            messengerRepository.deleteChat(chatId)

            events.document(eventId).delete().await()
        } catch (e: IOException) {
            println("Network error: $e")
        } catch (e: IllegalStateException) {
            println("Data conversion error: $e")
        } catch (e: FirebaseFirestoreException) {
            println("Firestore operation failed: ${e.code} - ${e.message}")
        } finally {
            events.document(eventId).delete().await()
            println("Event $eventId deleted")
        }
    }

    override suspend fun removeInvite(eventId: String, userId: String) {
        val user = userRepository.getUserById(userId)
        val event = getEventById(eventId)
        if (user == null || event == null) return
        val updatedUserInvites = user.invites.toMutableList().apply { remove(eventId) }
        database.collection("users")
            .document(userId)
            .update("invites", updatedUserInvites)
            .await()
        val updatedEventInvites = event.invites.toMutableList().apply { remove(userId) }
        events
            .document(eventId)
            .update("invites", updatedEventInvites)
            .await()
    }
    override suspend fun getParticipants(eventId: String): List<User> {
        return try {
            val document = events
                .document(eventId)
                .get()
                .await()

            if (document.exists()) {
                val participantsId = document.getStringList("participants")
                participantsId.let {
                    participantsId.mapNotNull { participantId ->
                        userRepository.getUserById(participantId)
                    }
                }
            } else {
                emptyList()
            }
        } catch (e: IOException) {
            println("Network error: $e")
            emptyList()
        } catch (e: IllegalStateException) {
            println("Data conversion error: $e")
            emptyList()
        } catch (e: FirebaseFirestoreException) {
            println("Firestore operation failed: ${e.code} - ${e.message}")
            emptyList()
        }
    }

    override fun observeEventsByUserId(userId: String): Flow<List<Event>> = callbackFlow {
        val userRef = database.collection("users").document(userId)

        val listener = userRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val eventIds = snapshot?.getStringList("events")
                launch {
                    val events = eventIds!!.mapNotNull { eventId ->
                        getEventById(eventId)
                    }
                    trySend(events)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun deleteParticipant(eventId: String, userId: String) {
        val eventRef = events.document(eventId)
        val eventDoc = eventRef.get().await()
        val userRef = database.collection("users").document(userId)
        val userDoc = userRef.get().await()

        try {
            if (!eventDoc.exists() || !userDoc.exists()) {
                println("Event with Id: $eventId  or User: $userId doesn't exists")
                return
            }
            userRef.update("events", FieldValue.arrayRemove(eventId))
                .await()
            eventRef
                .update("participants", FieldValue.arrayRemove(userId))
                .await()
            val participants = eventRef.get().await().getStringList("participants")
            if (participants.isEmpty()) deleteEvent(eventId)

            val chatId = "group_$eventId"
            messengerRepository.removeParticipantFromChat(chatId, userId)

            // Проверяем количество оставшихся участников
            val updatedEventDoc = eventRef.get().await()
            val remainingParticipants = updatedEventDoc.getStringList("participants")

            // Если участников не осталось, удаляем событие
            if (remainingParticipants.isEmpty()) {
                deleteEvent(eventId)
                println("Event $eventId deleted because no participants left")
            }
        } catch (e: IOException) {
            println("Network error at participant $userId delete: $e")
        } catch (e: IllegalStateException) {
            println("Data conversion error at participant $userId delete: $e")
        } catch (e: FirebaseFirestoreException) {
            println(
                "Firestore operation failed at participant $userId delete: ${e.code} - ${e.message}"
            )
        }
    }
}
