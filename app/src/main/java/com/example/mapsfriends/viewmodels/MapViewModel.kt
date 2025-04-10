package com.example.mapsfriends.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsfriends.data.utils.calculateMarkerSize
import com.example.mapsfriends.data.utils.createMarkerWithBorderAndTail
import com.example.mapsfriends.data.utils.loadOriginalBitmapFromUrl
import com.example.mapsfriends.data.mockdata.mockUsers
import com.example.mapsfriends.ui.screens.MarkerData
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    val markers = mutableStateListOf<MarkerData>()

    fun loadMarkersIntoMap(context: Context) {
        viewModelScope.launch {
            mockUsers.forEach { user ->
                val originalBitmap = loadOriginalBitmapFromUrl(context, user.avatarUrl)
                originalBitmap?.let {
                    val initialSize = calculateMarkerSize(18f) // начальный зум
                    markers.add(
                        MarkerData(
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
