package com.example.mapsfriends.utils

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.MarkerData
import com.example.mapsfriends.calculateMarkerSize
import com.example.mapsfriends.createMarkerWithBorderAndTail
import com.example.mapsfriends.loadOriginalBitmapFromUrl
import com.example.mapsfriends.mockUsers
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    val markers = mutableStateListOf<MarkerData>()
    val selectedMarkerId = mutableStateOf<Int?>(null)

    fun loadMarkersIntoMap(context: Context) {
        viewModelScope.launch {
            mockUsers.forEach { user ->
                val originalBitmap = loadOriginalBitmapFromUrl(context, user.avatarUrl)
                originalBitmap?.let {
                    val initialSize = calculateMarkerSize(18f) // начальный зум
                    markers.add(
                        MarkerData(
                            id = user.id,
                            position = user.location,
                            title = user.name,
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
}
