package com.example.mapsfriends

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val userFriendsRepository: UserFriendsRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val markers = mutableStateListOf<MarkerData>()
    val selectedMarkerId = mutableStateOf<String?>(null)

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser


    private val locationJobs = mutableMapOf<String, Job>()

    fun getUser(userId: String) {
        viewModelScope.launch {
            _selectedUser.value = userProfileRepository.getUserById(userId)
        }
    }

    fun setupMarkersAndObserveLocations(context: Context, userId: String, zoom: Float) {
        viewModelScope.launch {
            userFriendsRepository.observeFriendsList(userId)
                .collect { friends ->
                    val friendIds = friends.map { it.userId }

                    markers.removeAll { marker -> marker.id !in friendIds }

                    friends.forEach { user ->
                        if (user.userId.isBlank()) return@forEach

                        val existingIndex = markers.indexOfFirst { it.id == user.userId }

                        locationJobs[user.userId]?.cancel()

                        val locationJob = viewModelScope.launch {
                            userProfileRepository.observeLocation(user.userId)
                                .collect { newLocation ->
                                    updateMarkerPosition(user.userId, newLocation)
                                }
                        }
                        locationJobs[user.userId] = locationJob

                        val originalBitmap = loadOriginalBitmapFromUrl(context, user.avatarUrl)
                        originalBitmap?.let {
                            val initialSize = calculateMarkerSize(zoom)
                            val newMarker = MarkerData(
                                id = user.userId,
                                position = convertToLatLng(user.location),
                                title = user.username,
                                originalBitmap = it,
                                icon = BitmapDescriptorFactory.fromBitmap(
                                    createMarkerWithBorderAndTail(context, it, initialSize)
                                )
                            )

                            if (existingIndex != -1) {
                                markers[existingIndex] = newMarker
                            } else {
                                markers.add(newMarker)
                            }
                        }
                    }
                }
        }
    }

    fun updateMarkerIcons(currentZoom: Float, context: Context) {
        markers.forEach { marker ->
            val newSize = calculateMarkerSize(currentZoom)
            marker.icon = BitmapDescriptorFactory.fromBitmap(
                createMarkerWithBorderAndTail(context, marker.originalBitmap, newSize)
            )
        }
    }

    private fun updateMarkerPosition(userId: String, newLocation: GeoPoint) {
        val index = markers.indexOfFirst { it.id == userId }
        if (index != -1) {
            val updated = markers[index].copy(position = convertToLatLng(newLocation))
            markers[index] = updated
        }
    }

    private fun convertToLatLng(point: GeoPoint): LatLng {
        return LatLng(point.latitude, point.longitude)
    }

    override fun onCleared() {
        super.onCleared()
        locationJobs.values.forEach { it.cancel() }
        locationJobs.clear()
    }
}
