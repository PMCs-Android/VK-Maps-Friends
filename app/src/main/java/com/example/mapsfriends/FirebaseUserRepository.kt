package com.example.mapsfriends

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okio.IOException

class FirebaseUserRepository @Inject constructor(
    private val eventRepositoryProvider: javax.inject.Provider<EventRepository>
) : UserRepository {
    private val db = Firebase.firestore.collection("users")
    private val eventRepository: EventRepository
        get() = eventRepositoryProvider.get()

    companion object {
        private const val MAX_WHERE_IN_LIMIT = 10
    }

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
                val user = document.toObject(User::class.java)
                user
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

    override suspend fun updateUserLocation(userId: String, location: GeoPoint) {
        db
            .document(userId)
            .update("location", location)
    }

//    override suspend fun updateUserAvatar(userId: String, avatarUrl: String) {
//        db.document(userId)
//            .update("avatar_url", avatarUrl)
//    }

//    override suspend fun setFriendsFromVk(userId: String, listFriendsFromVk: List<String>) {
//        db
//            .document(userId)
//            .update("friends", listFriendsFromVk)
//            .await()
//    }

    override suspend fun setUser(
        userId: String,
        username: String,
        avatarUrl: String,
        friends: List<String>,
        location: GeoPoint
    ) {

        val existingUser = db.document(userId).get().await()
        if (existingUser.exists()) {
            return
        }

        val registeredFriends = friends.chunked(MAX_WHERE_IN_LIMIT).flatMap { chunk ->
            val snapshots = db.whereIn("user_id", chunk).get().await()
            snapshots.documents.mapNotNull { it.getString("user_id") }
        }

        val user = User(
            userId = userId,
            username = username,
            avatarUrl = avatarUrl,
            friends = registeredFriends,
            allFriends = friends,
            location = location
        )
        db.document(userId)
            .set(user)
            .await()
        val userFriendsInFirestore = db
            .whereArrayContains("allFriends", userId)
            .get()
            .await()
        for (doc in userFriendsInFirestore) {
            val friendId = doc.get("userId") as? String ?: continue
            val currentFriends = doc.get("friends") as? List<String> ?: emptyList()

            if (!currentFriends.contains(userId)) {
                val updatedFriends = currentFriends + userId
                db.document(friendId)
                    .update("friends", updatedFriends)
                    .await()
            }
        }
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
                        getUserById(friendId)
                    }
                    callback(friends)
                }
            }
    }

    override suspend fun observeInvites(userId: String, callback: (List<Event>) -> Unit) {
        db.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val eventsId = snapshot?.get("invites")
                    as? List<String> ?: return@addSnapshotListener
                CoroutineScope(Dispatchers.IO).launch {
                    val events = eventsId.mapNotNull { eventId ->
                        eventRepository.getEventById(eventId)
                    }
                    callback(events)
                }
            }
    }

    override suspend fun acceptInvite(userId: String, eventId: String) {
        eventRepository.removeInvite(eventId, userId)
        eventRepository.addParticipant(eventId, userId)
    }

    override suspend fun declineInvite(userId: String, eventId: String) {
        eventRepository.removeInvite(eventId, userId)
    }
}
