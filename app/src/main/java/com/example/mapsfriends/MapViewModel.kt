package com.example.mapsfriends

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.rememberCameraPositionState
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
                                Bitmap.createScaledBitmap(it, initialSize, initialSize, false)
                            )
                        )
                    )
                }
            }
        }
    }

    fun updateMarkerIcons(currentZoom: Float) {
        markers.forEach { marker ->
            val newSize = calculateMarkerSize(currentZoom)
            marker.icon = BitmapDescriptorFactory.fromBitmap(
                Bitmap.createScaledBitmap(marker.originalBitmap, newSize, newSize, false)
            )
        }
    }
}
