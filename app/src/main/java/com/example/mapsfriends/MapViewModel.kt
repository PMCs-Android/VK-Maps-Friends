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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    val markers = mutableStateListOf<MarkerData>()
    val selectedMarkerId = mutableStateOf<String?>(null)
    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser

    fun getUser(userId: String) {
        viewModelScope.launch {
            _selectedUser.value = userRepository.getUserById(userId)
        }
    }

    fun setupMarkersAndObserveLocations(context: Context, userId: String, zoom: Float) {
        viewModelScope.launch {
            loadMarkersIntoMap(context, userId, zoom)
        }
    }

    private suspend fun loadMarkersIntoMap(context: Context, userId: String, zoom: Float) {
        userRepository.observeFriendsList(userId) { friends ->
            viewModelScope.launch {
                val friendIds = friends?.map { it.userId } ?: emptyList()

                markers.removeAll { marker -> marker.id !in friendIds }

                friends?.forEach { user ->
                    val existingIndex = markers.indexOfFirst { it.id == user.userId }
                    userRepository.observeLocation(user.userId) { newLocation ->
                        updateMarkerPosition(user.userId, newLocation)
                    }

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
        val markerIndex = markers.indexOfFirst { it.id == userId }
        if (markerIndex != -1) {
            val updatedMarker = markers[markerIndex].copy(position = convertToLatLng(newLocation))
            markers[markerIndex] = updatedMarker
        }
    }

    private fun convertToLatLng(point: GeoPoint): LatLng {
        return LatLng(point.latitude, point.longitude)
    }
}
