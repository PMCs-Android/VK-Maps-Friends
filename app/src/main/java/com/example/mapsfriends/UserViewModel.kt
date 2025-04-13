package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    // StateFlow для хранения списка друзей
    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> = _friends.asStateFlow()

    fun loadFriends(userId: String) {
        viewModelScope.launch {
            try {
                val friendsList = userRepository.getFriendsList(userId) ?: emptyList()
                _friends.value = friendsList
            } catch (e: Exception) {
                println("Error creating event: ${e.message}")
                _friends.value = emptyList()
            }
        }
    }

    fun setUser(user : User) {
        viewModelScope.launch {
            try {
                userRepository.setUser(user.userId, user.username, user.avatarUrl, user.friends, user.location)
            } catch (e: Exception) {
                println("Error set user: ${e.message}")
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
