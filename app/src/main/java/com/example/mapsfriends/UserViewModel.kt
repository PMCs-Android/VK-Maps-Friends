package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.login.AuthTokenManager
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val userFriendsRepository: UserFriendsRepository,
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> = _friends.asStateFlow()
    private val _avatars = MutableStateFlow<Map<String, String>>(emptyMap())
    val avatars: StateFlow<Map<String, String>> = _avatars
    private val _avatarsPerEvent = MutableStateFlow<Map<String, Map<String, String>>>(emptyMap())
    val avatarsPerEvent: StateFlow<Map<String, Map<String, String>>> = _avatarsPerEvent

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        viewModelScope.launch {
            val userId = tokenManager.getUserId()
            if (userId != null) {
                val user = userProfileRepository.getUserById(userId)
                _currentUser.value = user
            }
        }
    }
//    fun getUser(userId: String) {
//        viewModelScope.launch {
//            _currentUser.value = userProfileRepository.getUserById(userId)!!
//        }
//    }

    fun startObservingUserFriends() {
        viewModelScope.launch {
            val userId = currentUser.filterNotNull().first().userId
            userFriendsRepository.observeFriendsList(userId)
                .catch { e ->
                    println("Error observing events: ${e.message}")
                    _friends.value = emptyList()
                }
                .collect { eventList ->
                    _friends.value = eventList
                }
        }
    }

    fun loadFriends(userId: String) {
        viewModelScope.launch {
            try {
                val friendsList = userFriendsRepository.getFriendsList(userId) ?: emptyList()
                _friends.value = friendsList
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading friends: ${e.message}")
                _friends.value = emptyList()
            } catch (e: IOException) {
                println("Network error loading friends: ${e.message}")
                _friends.value = emptyList()
            } catch (e: IllegalStateException) {
                println("Data error loading friends: ${e.message}")
                _friends.value = emptyList()
            }
        }
    }

    fun loadAvatarsForEventCard(eventId: String, userIds: List<String>) {
        viewModelScope.launch {
            val avatars = userProfileRepository.getUserAvatars(userIds)
                .filterValues { it != null } as Map<String, String>
            _avatarsPerEvent.update { it + (eventId to avatars) }
        }
    }

    fun loadAvatars(userIds: List<String>) {
        viewModelScope.launch {
            try {
                val result = userProfileRepository.getUserAvatars(userIds)
                _avatars.value = result.filterValues { it != null } as Map<String, String>
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading avatars: ${e.message}")
                _avatars.value = emptyMap()
            } catch (e: IOException) {
                println("Network error loading avatars: ${e.message}")
                _avatars.value = emptyMap()
            }
        }
    }
}
