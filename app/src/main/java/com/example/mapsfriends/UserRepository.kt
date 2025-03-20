package com.example.mapsfriends

import com.google.firebase.firestore.GeoPoint

data class User(
    val user_id: String = "",
    val username: String = "",
    val avatar_url: String = "",
    val friends: List<String> = emptyList<String>(),
    val location: GeoPoint = GeoPoint(0.0,0.0)
)

interface UserRepository {
    suspend fun getUserById (userId: String):User?

    suspend fun getFriendsList(userId: String): List<User>?

    suspend fun updateUserLocation(userId: String,location: GeoPoint)

    suspend fun updateUserAvatar(userId: String,avatarUrl: String)

    suspend fun setFriendsFromVk(userId: String,listFriendsFromVk: List<String>)

    suspend fun setUser(userId: String,username: String,avatarUrl: String,friends: List<String>,location: GeoPoint)


}