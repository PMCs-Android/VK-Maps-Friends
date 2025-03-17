package com.example.mapsfriends

import com.google.firebase.firestore.GeoPoint

data class Event (
    val event_id: String = "",
    val creator_id:String = "",
    val title:String = "",
    val description: String = "",
    val location: GeoPoint = GeoPoint(0.0,0.0),
    val time: String = "",
    val participants: List<String> = emptyList<String>()
        )

interface EventRepository{
    suspend fun createEvent(event_id: String,
                            creator_id: String,
                            title: String,
                            description: String,
                            location: GeoPoint,
                            time: String,
                            participants: List<String>)

    suspend fun addParticipant(eventId: String, userId:String)


}