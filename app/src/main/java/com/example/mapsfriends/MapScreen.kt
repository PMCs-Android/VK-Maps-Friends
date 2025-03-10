package com.example.mapsfriends

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch



@Composable
@Preview
fun MapScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val currentUser = remember {
        mockUsers.firstOrNull { it.id == 1 } ?: mockUsers.first()
    }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentUser.location, 18f)
    }


    val markers = remember { mutableStateListOf<MarkerData>() }


    LaunchedEffect(Unit) {
        mockUsers.forEach { user ->
            coroutineScope.launch {
                val originalBitmap = loadOriginalBitmapFromUrl(context, user.avatarUrl)
                originalBitmap?.let {
                    val initialSize = calculateMarkerSize(cameraPositionState.position.zoom)
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


    LaunchedEffect(cameraPositionState.position.zoom) {
        val currentZoom = cameraPositionState.position.zoom
        markers.forEach { marker ->
            val newSize = calculateMarkerSize(currentZoom)
            marker.icon = BitmapDescriptorFactory.fromBitmap(
                createMarkerWithBorderAndTail(context, marker.originalBitmap, newSize)
            )
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            properties = MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            ),
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false
            )
        ) {
            markers.forEach { markerData ->
                markerData.icon?.let {
                    Marker(
                        state = MarkerState(position = markerData.position),
                        title = markerData.title,
                        icon = it,
                        onClick = { false }
                    )
                }
            }
        }

        ZoomSlider(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            onZoomChange = { newZoom ->
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.zoomTo(newZoom),
                        150
                    )
                }
            },
            initialZoom = cameraPositionState.position.zoom
        )
    }
}
