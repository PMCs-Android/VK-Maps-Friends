package com.example.mapsfriends

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestoreException
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
            println("Network error: $e")
            null
        } catch (e: IllegalStateException) {
            println("Data conversion error: $e")
            null
        } catch (e: FirebaseFirestoreException) {
            println("Firestore operation failed: ${e.code} - ${e.message}")
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
            println("Network error: $e")
            null
        } catch (e: IllegalStateException) {
            println("Data conversion error: $e")
            null
        } catch (e: FirebaseFirestoreException) {
            println("Firestore operation failed: ${e.code} - ${e.message}")
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
        try {
            val userData = hashMapOf(
                "user_id" to userId,
                "username" to username,
                "avatar_url" to avatarUrl,
                "friends" to friends,
                "location" to location
            )

            db.document(userId)
                .set(userData)
                .await()

            println("User saved with ID: $userId")
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error saving user: ${e.message}")
        } catch (e: IOException) {
            println("Network error saving user: ${e.message}")
        } catch (e: IllegalStateException) {
            println("Data error saving user: ${e.message}")
        }
    }

    override suspend fun observeLocation(userId: String, callback: (GeoPoint) -> Unit) {
        db
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Ошибка подписки: ${error.message}")
                    return@addSnapshotListener
                }

                val newLocation = snapshot?.getGeoPoint("location")
                if (newLocation != null) {
                    callback(newLocation)
                }
            }
    }

    override suspend fun addFreind(userId: String, friendId: String) {
        if (userId == friendId) {
            return
        }
        Firebase.firestore.runTransaction { transaction ->
            val userDocument = transaction.get(db.document(userId))
            if (!userDocument.exists()) {
                throw NoSuchElementException("User with ID $userId not found")
            }

            val friendDocument = transaction.get(db.document(friendId))

            if (!friendDocument.exists()) {
                throw NoSuchElementException("Friend with ID $friendId not found")
            }

            val currentFriends = userDocument.get("friends") as? List<String> ?: emptyList()

            if (currentFriends.contains(friendId)) {
                throw IllegalStateException("User $userId already has friend $friendId")
            }

            transaction.update(
                db.document(userId),
                "friends",
                currentFriends + friendId
            )
        }
    }

    override suspend fun addEventToUser(creatorID: String, eventId: String) {
        try {
            val userRef = db.document(creatorID)
            val currentEvents = userRef.get().await().get("events") as? List<String> ?: emptyList()
            userRef.update("events", currentEvents + eventId).await()
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error adding event to user: ${e.message}")
        } catch (e: IOException) {
            println("Network error adding event to user: ${e.message}")
        }
    }

    override suspend fun getUserAvatars(userIds: List<String>): Map<String, String?> {
        return try {
            val documents = db
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .await()

            documents.associate { doc ->
                doc.id to doc.getString("avatar_url")?.takeIf { it.isNotEmpty() }
            }
        } catch (e: FirebaseFirestoreException) {
            println("Firestore error getting avatars: ${e.message}")
            emptyMap()
        } catch (e: IOException) {
            println("Network error getting avatars: ${e.message}")
            emptyMap()
        }
    }
}
