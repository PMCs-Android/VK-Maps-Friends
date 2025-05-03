package com.example.mapsfriends

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseUserFriendsRepository : UserFriendsRepository {
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
                        FirebaseUserProfileRepository().getUserById(friendId)
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

    override suspend fun addFriend(userId: String, friendId: String) {
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

    override suspend fun observeFriendsList(
        userId: String,
        callback: (List<User>) -> Unit
    ) {
        db.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FriendObserver", "Ошибка: ${error.message}")
                    return@addSnapshotListener
                }

                val friendIds = snapshot?.get("friends")
                        as? List<String> ?: return@addSnapshotListener

                CoroutineScope(Dispatchers.IO).launch {
                    val friends = friendIds.mapNotNull { friendId ->
                        FirebaseUserProfileRepository().getUserById(friendId)
                    }
                    callback(friends)
                }
            }
    }

    override suspend fun setFriendsFromVk(userId: String, listFriendsFromVk: List<String>) {
        db
            .document(userId)
            .update("friends", listFriendsFromVk)
            .await()
    }
}
