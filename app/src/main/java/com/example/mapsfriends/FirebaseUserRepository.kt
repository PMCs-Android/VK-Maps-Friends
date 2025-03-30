package com.example.mapsfriends

import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseUserRepository : UserRepository {
    private val db = Firebase.firestore.collection("users")

    override suspend fun getFriendsList(userId: String): List<User>? {
        return try {
            val document = db
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val friendsId = document.get("friends") as? List<String>
                friendsId?.let {
                    friendsId.mapNotNull { friendId ->
                        getUserById(friendId)
                    }
                }
            } else {
                null
            }
        } catch (e: IOException) {
            println(e)
            null
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return try {
            val document = db
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val user = User.fromFirestore(document.data!!)
                println("User from Firestore: $user")
                user
            } else {
                println("User not found: ")
                null
            }
        } catch (e: IOException) {
            println(e)
            null
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun updateUserLocation(userId: String, location: GeoPoint) {
        db
            .document(userId)
            .update("location", location)
    }

    override suspend fun updateUserAvatar(userId: String, avatarUrl: String) {
        db.document(userId)
            .update("avatar_url", avatarUrl)
    }

    override suspend fun setFriendsFromVk(userId: String, listFriendsFromVk: List<String>) {
        db
            .document(userId)
            .update("friends", listFriendsFromVk)
            .await()
    }

    override suspend fun setUser(
        userId: String,
        username: String,
        avatarUrl: String,
        friends: List<String>,
        location: GeoPoint
    ) {
        val user = User(
            userId = userId,
            username = username,
            avatarUrl = avatarUrl,
            friends = friends,
            location = location
        )

        db.document(userId)
            .set(user)
            .await()
    }
}
