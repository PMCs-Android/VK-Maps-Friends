package com.example.mapsfriends

import com.google.firebase.firestore.GeoPoint

data class Event(
    val eventId: String = "",
    val creatorId: String = "",
    val title: String = "",
    val description: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val time: String = "",
    val participants: List<String> = emptyList()
) {
    companion object {
        fun fromFirestore(map: Map<String, Any>): Event {
            return Event(
                eventId = map["event_id"] as? String ?: "",
                creatorId = map["creator_id"] as? String ?: "",
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                location = map["location"] as? GeoPoint ?: GeoPoint(0.0, 0.0),
                time = map["time"] as? String ?: "",
                participants = map["participants"] as? List<String> ?: emptyList()
            )
        }
    }

    fun toFirestore(): Map<String, Any> {
        return mapOf(
            "event_id" to eventId,
            "creator_id" to creatorId,
            "title" to title,
            "description" to description,
            "location" to location,
            "time" to time,
            "participants" to participants
        )
    }
}

interface EventRepository {
    suspend fun createEvent(event: Event)
    suspend fun getEventById(eventId: String): Event?
    suspend fun addParticipant(eventId: String, userId: String)
}