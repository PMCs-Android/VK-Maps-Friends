package com.example.mapsfriends

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseEventRepository : EventRepository {
    private val events = Firebase.firestore.collection("events")
    private val database = Firebase.firestore

    override suspend fun createEvent(event: Event) {
        val newParticipants = if (event.participants.contains(event.creatorId)) {
            event.participants
        } else {
            event.participants + event.creatorId
        }

        events.document(event.eventId)
            .set(event.copy(participants = newParticipants))
            .await()
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
                val event = Event.fromFirestore(doc.data!!)
                println("Event from Firestore: $event")
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
            database.runTransaction { transaction ->
                val event = transaction.get(events.document(eventId))
                if(!event.exists()) {
                    throw NoSuchElementException("Event with ID $eventId doesn't exist")
                }
                events.document(eventId).delete()
            }
        } catch (e: IOException) {
            println("Network error: $e")
        } catch (e: IllegalStateException) {
            println("Data conversion error: $e")
        } catch (e: FirebaseFirestoreException) {
            println("Firestore operation failed: ${e.code} - ${e.message}")
        }
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
                       FirebaseUserRepository().getUserById(participantId)
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
            val document = events.whereArrayContains("participants",userId)
                .get()
                .await()
            document.documents.mapNotNull { doc ->
                Event.fromFirestore(doc.data!!)
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
