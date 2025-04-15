package com.example.mapsfriends

import com.google.firebase.firestore.GeoPoint

data class User(
    val userId: String = "",
    val username: String = "",
    val avatarUrl: String = "",
    val friends: List<String> = emptyList(),
    val location: GeoPoint = GeoPoint(0.0, 0.0)
) {
    companion object {
        fun fromFirestore(map: Map<String, Any>): User {
            return User(
                userId = map["user_id"] as? String ?: "",
                username = map["username"] as? String ?: "",
                avatarUrl = map["avatar_url"] as? String ?: "",
                friends = map["friends"] as? List<String> ?: emptyList(),
                location = map["location"] as? GeoPoint ?: GeoPoint(0.0, 0.0)
            )
        }
    }
    fun toFirestore(): Map<String, Any> {
        return mapOf(
            "user_id" to userId,
            "username" to username,
            "avatar_url" to avatarUrl,
            "friends" to friends,
            "location" to location
        )
    }
}

interface UserRepository {

    suspend fun getUserById(userId: String): User?

    suspend fun getFriendsList(userId: String): List<User>?

    suspend fun updateUserLocation(userId: String, location: GeoPoint)

    suspend fun updateUserAvatar(userId: String, avatarUrl: String)

    suspend fun setFriendsFromVk(userId: String, listFriendsFromVk: List<String>)

    suspend fun setUser(
        userId: String,
        username: String,
        avatarUrl: String,
        friends: List<String>,
        location: GeoPoint
    )
    suspend fun addEventToUser(creatorID: String, eventId: String)
    suspend fun observeLocation(userId: String, callback: (GeoPoint) -> Unit)
    suspend fun addFreind(userId: String, friendId: String)
    suspend fun getUserAvatars(userIds : List<String>) : Map<String, String?>
}
