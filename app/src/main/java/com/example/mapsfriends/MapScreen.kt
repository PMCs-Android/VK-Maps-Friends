package com.example.mapsfriends

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import ZoomSlider

@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentUser =
        remember {
            mockUsers.firstOrNull { it.id == 1 } ?: mockUsers.first()
        }

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentUser.location, 18f)
        }

    LaunchedEffect(Unit) {
        viewModel.loadMarkersIntoMap(context)
    }

    LaunchedEffect(cameraPositionState.position.zoom) {
        viewModel.updateMarkerIcons(cameraPositionState.position.zoom, context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            properties =
                MapProperties(
                    mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style),
                ),
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings =
                MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = false,
                ),
        ) {
            viewModel.markers.forEach { markerData ->
                markerData.icon?.let {
                    Marker(
                        state = MarkerState(position = markerData.position),
                        title = markerData.title,
                        icon = it,
                        onClick = { false },
                    )
                }
            }
        }

        ZoomSlider(
            modifier = Modifier.align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
            onZoomChange = { newZoom ->
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.zoomTo(newZoom),
                        150,
                    )
                }
            },
            initialZoom = cameraPositionState.position.zoom,
        )
    }
}
