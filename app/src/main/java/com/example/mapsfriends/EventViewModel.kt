package com.example.mapsfriends

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    private val _currentEvent = MutableStateFlow<Event?>(null)
    private val _participants = MutableStateFlow<List<User>>(emptyList())
    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    private val _avatars = MutableStateFlow<Map<String, String?>>(emptyMap())
    private val _titleError = mutableStateOf(false)
    private val _descriptionError = mutableStateOf(false)
    private val _selectedMonth = MutableStateFlow<Int?>(null)

    val selectedMonth: StateFlow<Int?> = _selectedMonth
    val events: StateFlow<List<Event>> = _events
    val currentEvent: StateFlow<Event?> = _currentEvent
    val participants: StateFlow<List<User>> = _participants
    val avatars: StateFlow<Map<String, String?>> = _avatars
    val titleError: MutableState<Boolean> = _titleError
    val descriptionError: MutableState<Boolean> = _descriptionError

    fun validateFields(): Boolean {
        val isValid = !currentEvent.value?.title.isNullOrBlank() &&
                !currentEvent.value?.description.isNullOrBlank()

        _titleError.value = currentEvent.value?.title.isNullOrBlank()
        _descriptionError.value = currentEvent.value?.description.isNullOrBlank()

        return isValid
    }

    fun filterEventsByMonth(month: Int) {
        _selectedMonth.value = month
        _events.value = _allEvents.value.filter { event->
            event.time.slice(3..4).toInt() == (month + 1)
        }
    }

    fun resetMonthFilter() {
        _selectedMonth.value = null
        _events.value = _allEvents.value
    }

    fun createNewEvent(creatorId: String) {
        _currentEvent.value = Event(
            eventId = UUID.randomUUID().toString(),
            creatorId = creatorId,
            title = "",
            description = "",
            location = GeoPoint(0.0, 0.0),
            time = "",
            participants = listOf(creatorId)
        )
        viewModelScope.launch {
            userProfileRepository.getUserById(creatorId)?.let { creator ->
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
                println("event id:${event.eventId}: user id:${event.creatorId}")
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

    fun loadEventsForUser(userId: String) {
        viewModelScope.launch {
            try {
                _allEvents.value = eventRepository.getEventsByUserId(userId)
                _events.value = _allEvents.value
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading events: ${e.message}")
                _events.value = emptyList()
            } catch (e: IOException) {
                println("Network error loading events: ${e.message}")
                _events.value = emptyList()
            }
        }
    }

    fun deleteEvent(eventId: String, creatorId: String) {
        viewModelScope.launch {
            try {
                eventRepository.deleteEvent(eventId)
                loadEventsForUser(creatorId)
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
