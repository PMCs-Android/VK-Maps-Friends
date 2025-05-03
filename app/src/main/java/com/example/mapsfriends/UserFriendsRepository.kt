package com.example.mapsfriends

interface UserFriendsRepository {
    suspend fun getFriendsList(userId: String): List<User>?
    suspend fun setFriendsFromVk(userId: String, listFriendsFromVk: List<String>)
    suspend fun addFriend(userId: String, friendId: String)
    suspend fun observeFriendsList(userId: String, callback: (List<User>) -> Unit)
}
