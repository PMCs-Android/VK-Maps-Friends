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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _currentEvent = MutableStateFlow<Event?>(null)
    private val _participants = MutableStateFlow<List<User>>(emptyList())
    private val _events = MutableStateFlow<List<Event>>(emptyList())

    val events: StateFlow<List<Event>> = _events
    val currentEvent: StateFlow<Event?> = _currentEvent
    val participants: StateFlow<List<User>> = _participants

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
            userRepository.getUserById(currentUser.userId)?.let { creator ->
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

    fun setEventLocation(location: GeoPoint) {
        _currentEvent.value = _currentEvent.value?.copy(location = location)
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
                userRepository.addEventToUser(event.creatorId, event.eventId)
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

    fun loadEventsForUser(userId: String) {
        viewModelScope.launch {
            try {
                _events.value = eventRepository.getEventsByUserId(userId)
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading events: ${e.message}")
                _events.value = emptyList()
            } catch (e: IOException) {
                println("Network error loading events: ${e.message}")
                _events.value = emptyList()
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                println("Success delete event a${eventId}a")
                eventRepository.deleteEvent(eventId)
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error deleting event: ${e.message}")
            } catch (e: IOException) {
                println("Network error deleting event: ${e.message}")
            }
        }
    }
}
