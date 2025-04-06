package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val currentUserId = MutableStateFlow<String?>(null)

    fun createEvent(
        title: String,
        description: String,
        location: GeoPoint,
        time: String,
        participants: List<String>
    ) {
        viewModelScope.launch {
            try {
                val creatorId = currentUserId.value ?: return@launch

                val eventId = UUID.randomUUID().toString()

                val event = Event(
                    eventId = eventId,
                    creatorId = creatorId,
                    title = title,
                    description = description,
                    location = location,
                    time = time,
                    participants = participants
                )

                eventRepository.createEvent(event)

                userRepository.addEventToUser(creatorId, eventId)
            } catch (e: Exception) {
                println("Error creating event: ${e.message}")
            }
        }
    }
}

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

