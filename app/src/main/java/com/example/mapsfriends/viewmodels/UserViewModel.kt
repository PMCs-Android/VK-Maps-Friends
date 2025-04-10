package com.example.mapsfriends.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mapsfriends.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel()
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
