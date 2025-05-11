package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitesViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
):ViewModel() {
    val invitesFlow: StateFlow<List<Event>> = userProfileRepository
        .observeInvites(currentUser.userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun acceptInvite (eventId:String){
        viewModelScope.launch {
            userProfileRepository.acceptInvite(currentUser.userId, eventId)
        }
    }

    fun declineInvite (eventId: String){
        viewModelScope.launch {
            userProfileRepository.declineInvite(currentUser.userId,eventId)
        }
    }

}