package com.example.mapsfriends

import kotlinx.coroutines.flow.Flow

interface UserFriendsRepository {
    fun observeFriendsList(userId: String): Flow<List<User>>
    suspend fun getFriendsList(userId: String): List<User>?
    suspend fun setFriendsFromVk(userId: String, listFriendsFromVk: List<String>)
    suspend fun addFriend(userId: String, friendId: String)
}
