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
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    val markers = mutableStateListOf<MarkerData>()
    val selectedMarkerId = mutableStateOf<String?>(null)

    fun setupMarkersAndObserveLocations(context: Context, userId: String) {
        viewModelScope.launch {
            loadMarkersIntoMap(context, userId)
            markers.forEach { marker ->
                userRepository.observeLocation(marker.id) { newLocation ->
                    updateMarkerPosition(marker.id, newLocation)
                }
            }
        }
    }

    private suspend fun loadMarkersIntoMap(context: Context, userId: String) {
        val friends = userRepository.getFriendsList(userId)
        friends?.forEach { user ->
            val originalBitmap = loadOriginalBitmapFromUrl(context, user.avatarUrl)
            originalBitmap?.let {
                val initialSize = calculateMarkerSize(18f) // start zoom
                markers.add(
                    MarkerData(
                        id = user.userId,
                        position = convertToLatLng(user.location),
                        title = user.username,
                        originalBitmap = it,
                        icon = BitmapDescriptorFactory.fromBitmap(
                            createMarkerWithBorderAndTail(context, it, initialSize)
                        )
                    )
                )
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
