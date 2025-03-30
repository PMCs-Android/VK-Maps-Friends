package com.example.mapsfriends

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseEventRepository : EventRepository {
    private val events = Firebase.firestore.collection("events")
    private val database = Firebase.firestore

    override suspend fun createEvent(
        event_id: String,
        creator_id: String,
        title: String,
        description: String,
        location: GeoPoint,
        time: String,
        participants: List<String>
    ) {
        val newParticipants: List<String>
        if (!participants.contains(creator_id)) {
            newParticipants = participants + creator_id
        } else {
            newParticipants = participants
        }
        val event = Event(event_id, creator_id, title, description, location, time, newParticipants)
        events.document(event_id)
            .set(event)
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
}
