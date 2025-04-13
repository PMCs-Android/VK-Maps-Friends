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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    val markers = mutableStateListOf<MarkerData>()
    val selectedMarkerId = mutableStateOf<String?>(null)

    fun loadMarkersIntoMap(context: Context, userId: String) {
        viewModelScope.launch {
            val friends = userRepository.getFriendsList(userId)
            println(friends)
            friends?.forEach { user ->
                val originalBitmap = loadOriginalBitmapFromUrl(context, user.avatarUrl)
                originalBitmap?.let {
                    val initialSize = calculateMarkerSize(18f) //start zoom
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
    }

    fun updateMarkerIcons(currentZoom: Float, context: Context) {
        markers.forEach { marker ->
            val newSize = calculateMarkerSize(currentZoom)
            marker.icon = BitmapDescriptorFactory.fromBitmap(
                createMarkerWithBorderAndTail(context, marker.originalBitmap, newSize)
            )
        }
    }

    private fun convertToLatLng(point: GeoPoint): LatLng {
        return LatLng(point.latitude, point.longitude)
    }
}
