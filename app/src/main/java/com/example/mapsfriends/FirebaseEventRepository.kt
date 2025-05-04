package com.example.mapsfriends

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseEventRepository @Inject constructor(
    private val userRepository: UserRepository
) : EventRepository {
    private val events = Firebase.firestore.collection("events")
    private val database = Firebase.firestore

    override suspend fun createEvent(event: Event) {

        events.document(event.eventId)
            .set(event)
            .await()
        event.invites.forEach { sendInvite(event.eventId, it) }
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
            val userInvites = user.get("invites") as? List<String> ?: emptyList()
            val eventInvites = event.get("invites") as? List<String> ?: emptyList()

            val participants = event.get("participants") as? List<String> ?: emptyList()

            if (participants.contains(userId)) return

            if (!userInvites.contains(eventId)) {
                database
                    .collection("users")
                    .document(userId)
                    .update("invites", userInvites + eventId)
            }
            if (!eventInvites.contains(userId)) {
                events
                    .document(eventId)
                    .update("invites", eventInvites + userId)
            }
        }
    }
    override suspend fun addParticipant(eventId: String, userId: String) {
        if (!database.collection("users").document(userId).get().await().exists()) {
            return
        }

        val currentParticipants = events.document(eventId)
            .get()
            .await()
            .get("participants") as? List<String> ?: emptyList()

        events.document(eventId)
            .update("participants", currentParticipants + userId)
            .await()
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
            if (!event.exists()) {
                throw NoSuchElementException("Event with ID $eventId doesn't exist")
            }

            val inviteIds = event.get("invites") as? List<String> ?: emptyList()
            if (inviteIds.isEmpty()) {
                println("No invites found for event $eventId")
            }

            events.document(eventId).delete().await()
            println("Event $eventId deleted")

            inviteIds.forEach { inviteId ->
                val userDoc = database.collection("users").document(inviteId).get().await()
                if (userDoc.exists()) {
                    val userInvites = userDoc.get("invites") as? List<String> ?: emptyList()
                    val updatedInvites = userInvites.filter { it != eventId } // Удаляем eventId

                    database.collection("users").document(inviteId)
                        .update("invites", updatedInvites).await()
                    println("Removed event $eventId from user invites for $inviteId")
                } else {
                    println("User $inviteId not found")
                }
            }
        } catch (e: IOException) {
            println("Network error: $e")
        } catch (e: IllegalStateException) {
            println("Data conversion error: $e")
        } catch (e: FirebaseFirestoreException) {
            println("Firestore operation failed: ${e.code} - ${e.message}")
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
                val participantsId = document.get("participants") as? List<String>
                participantsId?.let {
                    participantsId.mapNotNull { participantId ->
                        userRepository.getUserById(participantId)
                    }
                } ?: emptyList()
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

    override suspend fun getEventsByUserId(userId: String): List<Event> {
        return try {
            val document = events.whereArrayContains("participants", userId)
                .get()
                .await()
            document.documents.mapNotNull { doc ->
                doc.toObject(Event::class.java)
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
    override suspend fun deleteParticipant(eventId: String, userId: String) {
        try {
            val eventDocument = events.document(eventId)
                .get()
                .await()
            if (!eventDocument.exists()) {
                println("Event with Id: $eventId doesn't exists")
                return
            }

            val participants = eventDocument.get("participants") as? List<String> ?: emptyList()

            if (!participants.contains(userId)) {
                println("User with Id: $userId is not a participant of event: $eventId")
                return
            }
            val updatedParticipants = participants - userId

            events.document(eventId)
                .update("participants", updatedParticipants)
                .await()
        } catch (e: IOException) {
            println("Network error: $e")
        } catch (e: IllegalStateException) {
            println("Data conversion error: $e")
        } catch (e: FirebaseFirestoreException) {
            println("Firestore operation failed: ${e.code} - ${e.message}")
        }
    }
}
