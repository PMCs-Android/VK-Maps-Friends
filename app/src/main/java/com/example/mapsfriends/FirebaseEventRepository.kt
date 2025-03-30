package com.example.mapsfriends

import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import java.io.IOException
import kotlinx.coroutines.tasks.await

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
        } catch (e: Exception) {
            println("Unexpected error: $e")
            null
        }
    }
}
