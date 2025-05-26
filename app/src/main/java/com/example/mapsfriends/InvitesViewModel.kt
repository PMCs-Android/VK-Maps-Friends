package com.example.mapsfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.login.AuthTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InvitesViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val tokenManager: AuthTokenManager
) : ViewModel() {
    private val currentUserId: String = tokenManager.getUserId() ?: ""
    val invitesFlow: StateFlow<List<Event>> = userProfileRepository
        .observeInvites(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    suspend fun acceptInvite(eventId: String) {
        userProfileRepository.acceptInvite(currentUserId, eventId)
    }

    suspend fun declineInvite(eventId: String) {
        userProfileRepository.declineInvite(currentUserId, eventId)
    }
}
