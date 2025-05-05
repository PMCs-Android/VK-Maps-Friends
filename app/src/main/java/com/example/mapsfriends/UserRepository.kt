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

interface UserRepository {

    suspend fun getUserById(userId: String): User?

    suspend fun getFriendsList(userId: String): List<User>?

    suspend fun updateUserLocation(userId: String, location: GeoPoint)

    // suspend fun updateUserAvatar(userId: String, avatarUrl: String)

    // suspend fun setFriendsFromVk(userId: String, listFriendsFromVk: List<String>)

    suspend fun setUser(
        userId: String,
        username: String,
        avatarUrl: String,
        friends: List<String>,
        location: GeoPoint
    )

    fun observeLocation(userId: String): Flow<GeoPoint>
    suspend fun addFriend(userId: String, friendId: String)
    fun observeFriendsList(userId: String): Flow<List<User>>
    suspend fun acceptInvite(userId: String, eventId: String)
    suspend fun declineInvite(userId: String, eventId: String)
    fun observeInvites(userId: String): Flow<List<Event>>
}
