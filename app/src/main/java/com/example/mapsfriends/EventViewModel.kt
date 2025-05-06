package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userProfileRepository: UserProfileRepository,
    private val userFriendsRepository: UserFriendsRepository
) : ViewModel() {
    private val _currentEvent = MutableStateFlow<Event?>(null)
    private val _participants = MutableStateFlow<List<User>>(emptyList())
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    private val _avatars = MutableStateFlow<Map<String, String?>>(emptyMap())

   val eventsFlow: StateFlow<List<Event>> = eventRepository
       .observeEventsByUserId(currentUser.userId)
       .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentEvent: StateFlow<Event?> = _currentEvent
    val participants: StateFlow<List<User>> = _participants
    val avatars: StateFlow<Map<String, String?>> = _avatars

    fun createNewEvent() {
        _currentEvent.value = Event(
            eventId = UUID.randomUUID().toString(),
            creatorId = currentUser.userId,
            title = "",
            description = "",
            location = GeoPoint(0.0, 0.0),
            time = "",
            participants = listOf(currentUser.userId)
        )
        viewModelScope.launch {
            userProfileRepository.getUserById(currentUser.userId)?.let { creator ->
                _participants.value = listOf(creator)
            }
            println("create new event ${_participants.value.size}")
        }
    }

    fun setEventTitle(title: String) {
        _currentEvent.value = _currentEvent.value?.copy(title = title)
    }

    fun setEventDescription(description: String) {
        _currentEvent.value = _currentEvent.value?.copy(description = description)
    }

    fun setEventTime(time: String) {
        _currentEvent.value = _currentEvent.value?.copy(time = time)
    }

    fun saveCurrentEvent() {
        viewModelScope.launch {
            try {
                val event = _currentEvent.value ?: throw IllegalStateException("Event not created")
                println("event id:${event.eventId}:")
                eventRepository.createEvent(event)
                _participants.value
                    .filter { it.userId != event.creatorId }
                    .forEach { user ->
                        eventRepository.addParticipant(event.eventId, user.userId)
                    }
                loadParticipants(event.eventId)
                userProfileRepository.addEventToUser(event.creatorId, event.eventId)
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error saving event: ${e.message}")
            } catch (e: IOException) {
                println("Network error saving event: ${e.message}")
            } catch (e: IllegalStateException) {
                println("Data error saving event: ${e.message}")
            }
        }
    }

    fun addParticipant(userId: String) {
        viewModelScope.launch {
            try {
                val eventId = _currentEvent.value?.eventId
                    ?: throw IllegalStateException("Сначала создайте событие")
                eventRepository.addParticipant(eventId, userId)
                loadParticipants(eventId)
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error adding participant: ${e.message}")
            } catch (e: IOException) {
                println("Network error adding participant: ${e.message}")
            }
        }
    }

    suspend fun loadParticipants(eventId: String) {
        _participants.value = eventRepository.getParticipants(eventId)
    }


    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                eventRepository.deleteEvent(eventId)
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error deleting event: ${e.message}")
            } catch (e: IOException) {
                println("Network error deleting event: ${e.message}")
            }
        }
    }

    fun getEvent(eventId: String) {
        viewModelScope.launch {
            try {
                _currentEvent.value = eventRepository.getEventById(eventId)
//                _avatars.value = result.filterValues { it != null } as Map<String, String>
                _avatars.value = userProfileRepository.getUserAvatars(
                    _currentEvent.value?.participants
                        ?: emptyList()
                ).filterValues { it != null } as Map<String, String>
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading event: ${e.message}")
            } catch (e: IOException) {
                println("Network error loading event: ${e.message}")
            }
        }
    }
}
