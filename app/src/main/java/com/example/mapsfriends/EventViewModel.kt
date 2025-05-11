package com.example.mapsfriends

import android.util.Log
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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val currentEvent: StateFlow<Event?> = _currentEvent
    val participants: StateFlow<List<User>> = _participants
    val avatars: StateFlow<Map<String, String?>> = _avatars

    fun createNewEvent() {
        _currentEvent.value = Event(
            eventId = UUID.randomUUID().toString(),
            creatorId = currentUser.userId,
//            title = "",
//            description = "",
//            location = GeoPoint(0.0, 0.0),
//            time = "",
//            participants = listOf(currentUser.userId)
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
                val event = _currentEvent.value?.copy(
                    invites = _participants.value
                        .map { it.userId }
                        .filter { it != currentUser.userId }
                ) ?: throw IllegalStateException("Event not created")
                println("event id:${event.eventId}: Event: $event")
                eventRepository.createEvent(event)
//                _participants.value
//                    .filter { it.userId != event.creatorId }
//                    .forEach { user ->
//                        eventRepository.addParticipant(event.eventId, user.userId)
//                    }
                //userProfileRepository.addEventToUser(event.creatorId, event.eventId)
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
                val current = _participants.value.toMutableList()
                val user = userProfileRepository.getUserById(userId)
                if (user != null && current.none { it.userId == userId }) {
                    current.add(user)
                    _participants.value = current
                }
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error adding participant: ${e.message}")
            } catch (e: IOException) {
                println("Network error adding participant: ${e.message}")
            }
        }
    }

//    suspend fun loadParticipants(eventId: String) {
//        _participants.value = eventRepository.getParticipants(eventId)
//    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                eventRepository.deleteParticipant(eventId, currentUser.userId)
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

    fun tryDeleteIncompleteEvent() {
        val event = currentEvent.value ?: return
        viewModelScope.launch {
            if (event.title.isBlank() || event.time.isBlank()) {
                event.eventId.let { eventId ->
                    eventRepository.deleteEvent(eventId)
                }
                _currentEvent.value = null
                Log.d("EventViewModel", "Incomplete event deleted")
            }
        }
    }

    fun getShortDays(events:List<Event>):Map<String,String>{
        val year = LocalDate.now().year
        return events.associate { event ->
            val (datePart, _) = event.time.split(" ")
            val (day, month) = datePart.split(".").map { it.toInt() }
            val date = LocalDate.of(year,month,day)
            val shortDay = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).take(2)
            event.eventId to shortDay
        }
    }

}
