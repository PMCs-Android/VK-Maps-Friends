package com.example.mapsfriends

import com.google.firebase.firestore.GeoPoint

data class Event(
    val eventId: String = "",
    val creatorId: String = "",
    val title: String = "",
    val description: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val time: String = "",
    val participants: List<String> = listOf<String>(creatorId),
    val invites: List<String> = emptyList()
)

interface EventRepository {
    suspend fun createEvent(event: Event)
    suspend fun getEventById(eventId: String): Event?
    suspend fun addParticipant(eventId: String, userId: String)
    suspend fun deleteEvent(eventId: String)
    suspend fun deleteParticipant(eventId: String, userId: String)
    suspend fun getParticipants(eventId: String): List<User>
    suspend fun getEventsByUserId(userId: String): List<Event>
    suspend fun sendInvite(eventId: String, userId: String)
    suspend fun removeInvite(eventId: String, userId: String)
}
