package com.example.mapsfriends

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val currentUserId = MutableStateFlow<String?>(null)

    private val _eventId = MutableStateFlow(UUID.randomUUID().toString())
    val eventId: StateFlow<String> = _eventId

    private val _participants = MutableStateFlow<List<User>>(emptyList())
    val participants: StateFlow<List<User>> = _participants

    fun createEvent(
        eventId: String,
        title: String,
        description: String,
        location: GeoPoint,
        time: String,
        participants: List<String>
    ) {
        viewModelScope.launch {
            try {
                // CHANGE TO REAL CURRENT USER ID !!
                val creatorId = currentUser.userId

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
                loadParticipants()
            } catch (e: Exception) {
                println("Error creating event: ${e.message}")
            }
        }
    }

    fun addParticipant(userId: String) {
        viewModelScope.launch {
            try {
                eventRepository.addParticipant(_eventId.value, userId)
                // Обновляем локальный список
                val user = FirebaseUserRepository().getUserById(userId)
                user?.let {
                    _participants.value = _participants.value + it
                }
            } catch (e: Exception) {
                println("Ошибка добавления: ${e.message}")
            }
        }
    }

    suspend fun loadParticipants() {
        _participants.value = eventRepository.getParticipants(_eventId.value)
    }

    fun getEventsByUserId(userId: String): List<Event> {
        viewModelScope.launch {
            return@launch try {
                val events = eventRepository.getEventsByUserId(userId)
            } catch (e: Exception) {
                println("Error getting events of user: ${e.message}")
            }
        }
        return emptyList()
    }
}
