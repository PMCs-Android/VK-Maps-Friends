package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel()

/* {
    private val _currentUser = MutableStateFlow<Event?>(null)
    fun getUser(userId: String = "1") {
        viewModelScope.launch {
            _currentUser.value = eventRepository.getEventById(userId)
        }
    }
}

@Composable
fun Screen(
    userViewModel: EventViewModel = hiltViewModel()
) {
    Box(modifier = Modifier.fillMaxSize()){
    Button (modifier = Modifier.align(Alignment.Center),
        onClick = {userViewModel.getUser()}) { }
    }
}*/

