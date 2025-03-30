package com.example.mapsfriends

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseEventRepository : EventRepository {
    private val events = Firebase.firestore.collection("events")
    private val database = Firebase.firestore

    override suspend fun createEvent(event: Event) {
        val newParticipants = event.participants
        if (!newParticipants.contains(event.creatorId)) {
            newParticipants.plus(event.creatorId)
        }
        val newEvent = event.copy(participants = newParticipants)
        events.document(event.eventId)
            .set(newEvent)
            .await()
    }

    override suspend fun addParticipant(eventId: String, userId: String) {
        if (!database.collection("users").document(userId).get().await().exists()) {
            return
        }
        val currentParticipants = events.document(eventId)
            .get()
            .await()
            .get("participants") as List<String>
        val updatedParticipants = currentParticipants + userId

        events.document(eventId)
            .update("participants", updatedParticipants)
            .await()
    }

    override suspend fun getEventById(eventId: String): Event? {
        return try {
            val doc = events
                .document(eventId)
                .get()
                .await()

            if (doc.exists()){
                val event = Event.fromFirestore(doc.data!!)
                println("Event from Firestore: $event")
                event
            } else{
                null
            }
        } catch (e:IOException) {
            println(e)
            null
        } catch (e:Exception) {
            println(e)
            null
        }
    }
}
