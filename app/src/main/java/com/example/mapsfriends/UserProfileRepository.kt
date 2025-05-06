package com.example.mapsfriends

import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow

data class User(
    val userId: String = "",
    val username: String = "",
    val avatarUrl: String = "",
    val friends: List<String> = emptyList(),
    val allFriends: List<String> = emptyList(),
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val invites: List<String> = emptyList()
)

interface UserProfileRepository {
    suspend fun getUserById(userId: String): User?
    suspend fun updateUserLocation(userId: String, location: GeoPoint)
    suspend fun updateUserAvatar(userId: String, avatarUrl: String)
    suspend fun setUser(
        userId: String,
        username: String,
        avatarUrl: String,
        friends: List<String>,
        location: GeoPoint
    )
    suspend fun acceptInvite(userId: String, eventId: String)
    suspend fun declineInvite(userId: String, eventId: String)
    fun observeInvites(userId: String): Flow<List<Event>>
    fun observeLocation(userId: String): Flow<GeoPoint>
    suspend fun addEventToUser(creatorID: String, eventId: String)
    suspend fun getUserAvatars(userIds: List<String>): Map<String, String?>
}
