package com.example.mapsfriends

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.login.AuthTokenManager
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userProfileRepository: UserProfileRepository,
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val _currentEvent = MutableStateFlow<Event?>(null)
    private val _participants = MutableStateFlow<List<User>>(emptyList())
    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    private val _avatars = MutableStateFlow<Map<String, String?>>(emptyMap())
    private val _titleError = mutableStateOf(false)
    private val _descriptionError = mutableStateOf(false)
    private val _selectedMonth = MutableStateFlow<Int?>(null)
    private val currentUserId: String = tokenManager.getUserId() ?: ""

    val selectedMonth: StateFlow<Int?> = _selectedMonth
    val events: StateFlow<List<Event>> = _events

//    val eventsFlow: StateFlow<List<Event>> = eventRepository.observeEventsByUserId(currentUserId)
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val currentEvent: StateFlow<Event?> = _currentEvent
    val participants: StateFlow<List<User>> = _participants
    val avatars: StateFlow<Map<String, String?>> = _avatars
    val titleError: MutableState<Boolean> = _titleError
    val descriptionError: MutableState<Boolean> = _descriptionError

    fun validateFields(): Boolean {
        val isValid =
            !currentEvent.value?.title.isNullOrBlank() && !currentEvent.value?.description.isNullOrBlank()

        _titleError.value = currentEvent.value?.title.isNullOrBlank()
        _descriptionError.value = currentEvent.value?.description.isNullOrBlank()

        return isValid
    }

    fun filterEventsByMonth(month: Int) {
        _selectedMonth.value = month
        _events.value = _allEvents.value.filter { event ->
            event.time.slice(3..4).toInt() == (month + 1)
        }.sortedWith(
            compareBy(
                {
                    LocalDate.of(
                        LocalDate.now().year,
                        it.time.slice(3..4).toInt(),
                        it.time.slice(0..1).toInt()
                    )
                },
                {
                    LocalDate.of(
                        LocalDate.now().year,
                        it.time.slice(6..7).toInt(),
                        it.time.slice(9..10).toInt()
                    )
                }
            )
        )
    }

    fun resetMonthFilter() {
        _selectedMonth.value = null
        _events.value = _allEvents.value
    }

    fun createNewEvent() {
        _currentEvent.value = Event(
            eventId = UUID.randomUUID().toString(),
            creatorId = currentUserId,
        )
        viewModelScope.launch {
            userProfileRepository.getUserById(currentUserId)?.let { creator ->
                _participants.value = listOf(creator)
            }
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
                val event =
                    _currentEvent.value?.copy(
                        invites = _participants.value.map { it.userId }
                            .filter { it != currentUserId }
                    )
                        ?: throw IllegalStateException("Event not created")
                println("event id:${event.eventId}: Event: $event")
                eventRepository.createEvent(event)
                _participants.value.filter { it.userId != event.creatorId }.forEach { user ->
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

    suspend fun loadParticipants(eventId: String) {
        _participants.value = eventRepository.getParticipants(eventId)
    }

    fun loadEventsForUser(userId: String) {
        viewModelScope.launch {
            eventRepository.observeEventsByUserId(userId).catch { e ->
                when (e) {
                    is FirebaseFirestoreException -> {
                        println("Firestore error loading events: ${e.message}")
                    }

                    is IOException -> {
                        println("Network error loading events: ${e.message}")
                    }

                    else -> {
                        println("Unknown error loading events: ${e.message}")
                    }
                }
                _events.value = emptyList()
                _allEvents.value = emptyList()
            }.collect { events ->
                val sortedEvents = events.sortedWith(
                    compareBy<Event> {
                        LocalDate.of(
                            LocalDate.now().year,
                            it.time.slice(3..4).toInt(),
                            it.time.slice(0..1).toInt()
                        )
                    }.thenBy {
                        LocalDate.of(
                            LocalDate.now().year,
                            it.time.slice(6..7).toInt(),
                            it.time.slice(9..10).toInt()
                        )
                    }
                )
                _allEvents.value = sortedEvents
                _events.value = sortedEvents
            }
        }
    }

//    fun loadEventsForUser(userId: String) {
//        viewModelScope.launch {
//            try {
//                _events.value = eventRepository.getEventsByUserId(userId)
//            } catch (e: FirebaseFirestoreException) {
//                println("Firestore error loading events: ${e.message}")
//                _events.value = emptyList()
//            } catch (e: IOException) {
//                println("Network error loading events: ${e.message}")
//                _events.value = emptyList()
//            }
//        }
//    }

    fun deleteEvent(eventId: String, creatorId: String) {
        viewModelScope.launch {
            try {
                eventRepository.deleteParticipant(eventId, currentUserId)
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
                    _currentEvent.value?.participants ?: emptyList()
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

    fun getShortDays(events: List<Event>): Map<String, String> {
        val year = LocalDate.now().year
        return events.associate { event ->
            val (datePart, _) = event.time.split(" ")
            val (day, month) = datePart.split(".").map { it.toInt() }
            val date = LocalDate.of(year, month, day)
            val shortDay = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).take(2)
            event.eventId to shortDay
        }
    }
}
