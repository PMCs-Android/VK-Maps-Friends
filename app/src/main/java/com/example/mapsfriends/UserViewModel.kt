package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> = _friends.asStateFlow()
    private val _avatars = MutableStateFlow<Map<String, String>>(emptyMap())
    val avatars: StateFlow<Map<String, String>> = _avatars

    fun loadFriends(userId: String) {
        viewModelScope.launch {
            try {
                val friendsList = userRepository.getFriendsList(userId) ?: emptyList()
                _friends.value = friendsList
            } catch (e: Error) {
                println("Error creating event: ${e.message}")
                _friends.value = emptyList()
            }
        }
    }

    fun setUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.setUser(
                    user.userId,
                    user.username,
                    user.avatarUrl,
                    user.friends,
                    user.location
                )
            } catch (e: Error) {
                println("Error set user: ${e.message}")
            }
        }
    }

    fun loadAvatars(userIds: List<String>) {
        viewModelScope.launch {
            try {
                val result = userRepository.getUserAvatars(userIds)
                _avatars.value = result.filterValues { it != null } as Map<String, String>
            } catch (e: Error) {
                println("Error load avatars ${e.message}")
            }
        }
    }
}
/* {
    private val _currentUser = MutableStateFlow<User?>(null)
    fun getUser(userId: String = "3") {
        viewModelScope.launch {
            _currentUser.value = userRepository.getUserById(userId)
        }
    }
}

@Composable
fun Screen(
    userViewModel: UserViewModel = hiltViewModel()
) {
    Box(modifier = Modifier.fillMaxSize()){
    Button(modifier = Modifier.align(Alignment.Center),
        onClick = {userViewModel.getUser()}) { }
    }
}*/
