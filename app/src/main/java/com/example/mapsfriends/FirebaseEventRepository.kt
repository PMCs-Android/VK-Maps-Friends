package com.example.mapsfriends

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseEventRepository @Inject constructor(
    private val userRepository: UserProfileRepository
) : EventRepository {
    private val events = Firebase.firestore.collection("events")
    private val database = Firebase.firestore

    override suspend fun createEvent(event: Event) {
        events.document(event.eventId)
            .set(event)
            .await()

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
        val userRef = Firebase.firestore.collection("users").document(userId)

        if (!userRef.get().await().exists()) {
            return
        }

        events.document(eventId)
            .update("participants", FieldValue.arrayUnion(userId))
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

            val inviteIds = event.getStringList("invites")
            if (inviteIds.isEmpty()) {
                println("No invites found for event $eventId")
            }

            events.document(eventId).delete().await()
            println("Event $eventId deleted")

            inviteIds.forEach { inviteId ->
                val userDoc = database.collection("users").document(inviteId).get().await()
                if (userDoc.exists()) {

                    database.collection("users").document(inviteId)

                        .update("invites", FieldValue.arrayRemove(inviteId))
                        .await()
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
        val listener = events
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val eventList = snapshot?.documents?.mapNotNull { it.toObject(Event::class.java) }
                trySend(eventList ?: emptyList())
            }

        awaitClose { listener.remove() }
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

            val participants = eventDocument.getStringList("participants")

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
