package com.example.mapsfriends

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseUserProfileRepository : UserProfileRepository {
    private val db = Firebase.firestore.collection("users")

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

    override suspend fun observeLocation(userId: String, callback: (GeoPoint) -> Unit) {
        db
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("LocationObserver", "Ошибка подписки: ${error.message}")
                    return@addSnapshotListener
                }

                val newLocation = snapshot?.getGeoPoint("location")
                if (newLocation != null) {
                    callback(newLocation)
                }
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

    companion object
}
