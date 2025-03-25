package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel(){
    val list = listOf<String>("3","4")
    fun setEvent(){
        viewModelScope.launch{
            eventRepository.addParticipant("1","1")
        }
    }
}