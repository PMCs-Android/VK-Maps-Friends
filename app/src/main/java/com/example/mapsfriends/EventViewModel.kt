package com.example.mapsfriends

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.login.AuthTokenManager
import com.example.mapsfriends.messenger.MessengerRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userProfileRepository: UserProfileRepository,
    private val messengerRepository: MessengerRepository,
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
    private val _dateError = mutableStateOf(false)
    private val _timeError = mutableStateOf(false)
    private val _availableFriends = MutableStateFlow<List<User>>(emptyList())
    private val currentUserId: String = tokenManager.getUserId() ?: ""

    val selectedMonth: StateFlow<Int?> = _selectedMonth
    val events: StateFlow<List<Event>> = _events

    val currentEvent: StateFlow<Event?> = _currentEvent
    val participants: StateFlow<List<User>> = _participants
    val avatars: StateFlow<Map<String, String?>> = _avatars
    val titleError: MutableState<Boolean> = _titleError
    val descriptionError: MutableState<Boolean> = _descriptionError
    val dateError: MutableState<Boolean> = _dateError
    val timeError: MutableState<Boolean> = _timeError
    val availableFriends: StateFlow<List<User>> = _availableFriends

    fun validateFields(): Boolean {
        val isValid =
            !currentEvent.value?.title.isNullOrBlank() &&
                !currentEvent.value?.description.isNullOrBlank() &&
                currentEvent.value?.time?.length == Dimensions.ELEVEN

        _titleError.value = currentEvent.value?.title.isNullOrBlank()
        _descriptionError.value = currentEvent.value?.description.isNullOrBlank()
        currentEvent.value?.time?.length?.let { _dateError.value = it <= Dimensions.BORDER_WIDTH }
        currentEvent.value?.time?.length?.let {
            _timeError.value = it <= Dimensions.SMALL_PADDING_1
        }
        return isValid
    }

    fun filterEventsByMonth(month: Int) {
        _selectedMonth.value = month
        _events.value = _allEvents.value.filter { event ->
            event.time.slice(Dimensions.THREE..Dimensions.BORDER_WIDTH).toInt() == (month + 1)
        }.sortedWith(
            compareBy(
                {
                    LocalDate.of(
                        LocalDate.now().year,
                        it.time.slice(Dimensions.THREE..Dimensions.BORDER_WIDTH).toInt(),
                        it.time.slice(0..1).toInt()
                    )
                },
                {
                    LocalDate.of(
                        LocalDate.now().year,
                        it.time.slice(Dimensions.SIX..Dimensions.SEVEN).toInt(),
                        it.time.slice(Dimensions.NINE..Dimensions.SMALL_PADDING_1).toInt()
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

    fun setEventLocation(location: GeoPoint) {
        _currentEvent.value = _currentEvent.value?.copy(location = location)
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
                            it.time.slice(Dimensions.THREE..Dimensions.BORDER_WIDTH).toInt(),
                            it.time.slice(0..1).toInt()
                        )
                    }.thenBy {
                        LocalDate.of(
                            LocalDate.now().year,
                            it.time.slice(Dimensions.SIX..Dimensions.SEVEN).toInt(),
                            it.time.slice(Dimensions.NINE..Dimensions.SMALL_PADDING_1).toInt()
                        )
                    }
                )
                _allEvents.value = sortedEvents
                _events.value = sortedEvents
            }
        }
    }

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

    fun loadAvailableFriends() {
        viewModelScope.launch {
            try {
                val currentEvent = _currentEvent.value ?: return@launch
                val userFriends = userProfileRepository
                    .getUserById(currentUserId)?.friends ?: emptyList()

                val availableFriendsList = userFriends.mapNotNull { friendId ->
                    userProfileRepository.getUserById(friendId)
                }.filter { friend ->
                    val isNotParticipant = !currentEvent.participants.contains(friend.userId)
                    val isNotInvited = !currentEvent.invites.contains(friend.userId)
                    isNotParticipant && isNotInvited
                }

                _availableFriends.value = availableFriendsList
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading event: ${e.message}")
            } catch (e: IOException) {
                println("Network error loading event: ${e.message}")
            }
        }
    }

    fun inviteFriendToEvent(friendId: String) {
        viewModelScope.launch {
            try {
                val event = _currentEvent.value ?: return@launch
                eventRepository.sendInvite(event.eventId, friendId)

                // Update available friends list
                _availableFriends.value = _availableFriends.value.filter { it.userId != friendId }

                // Refresh event data
                getEvent(event.eventId)
            } catch (e: FirebaseFirestoreException) {
                println("Firestore error loading event: ${e.message}")
            } catch (e: IOException) {
                println("Network error loading event: ${e.message}")
            }
        }
    }

    suspend fun getOrCreateGroupChat(event: Event): String {
        try {
            val chatId = "group_${event.eventId}"
            val existingChat = messengerRepository.getChatById(chatId)
            if (existingChat != null) {
                if (existingChat.participants != event.participants) {
                    messengerRepository.updateChatParticipants(chatId, event.participants)
                    Log.d("EventViewModel", "Updated participants for chat: $chatId")
                }
                Log.d("EventViewModel", "Found existing group chat: $chatId")
                return chatId
            }
            // Создаем новый чат
            val newChatId = messengerRepository.createGroupChat(event.participants, event.eventId)
            Log.d("EventViewModel", "Created new group chat: $newChatId")
            return newChatId
        } catch (e: Exception) {
            Log.e("EventViewModel", "Error getting/creating group chat: ${e.message}", e)
            return ""
        }
    }
}
