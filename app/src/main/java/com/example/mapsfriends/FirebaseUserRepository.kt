package com.example.mapsfriends

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                val friendsId = document.getStringList("friends")
                friendsId.let {
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
            val currentFriends = doc.getStringList("friends")

            if (!currentFriends.contains(userId)) {
                val updatedFriends = currentFriends + userId
                db.document(friendId)
                    .update("friends", updatedFriends)
                    .await()
            }
        }
    }

    override fun observeLocation(userId: String): Flow<GeoPoint> = callbackFlow {
        val listener = db
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val newLocation = snapshot?.getGeoPoint("location")
                if (newLocation != null) {
                    trySend(newLocation).isSuccess
                }
            }
        awaitClose { listener.remove() }
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

            val currentFriends = userDocument.getStringList("friends")

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

    override fun observeFriendsList(userId: String): Flow<List<User>> = callbackFlow {
        val listener = db.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val friendIds = snapshot?.getStringList("friends") ?: return@addSnapshotListener
                launch {
                    val friends = friendIds.mapNotNull { friendId ->
                        getUserById(friendId)
                    }
                    trySend(friends).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    override fun observeInvites(userId: String): Flow<List<Event>> = callbackFlow {
        val listener = db.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val eventsId = snapshot?.getStringList("invites")
                    ?: return@addSnapshotListener
                launch {
                    val events = eventsId.mapNotNull { eventId ->
                        eventRepository.getEventById(eventId)
                    }
                    trySend(events).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun acceptInvite(userId: String, eventId: String) {
        eventRepository.removeInvite(eventId, userId)
        eventRepository.addParticipant(eventId, userId)
    }

    override suspend fun declineInvite(userId: String, eventId: String) {
        eventRepository.removeInvite(eventId, userId)
    }
}
