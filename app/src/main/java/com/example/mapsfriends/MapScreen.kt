package com.example.mapsfriends

import ZoomSlider
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
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    navController: NavHostController,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser = remember { mockUsers.firstOrNull { it.id == "1" } ?: mockUsers.first() }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentUser.location, 18f)
    }

    LaunchedEffect(Unit) {
        viewModel.setupMarkersAndObserveLocations(context, currentUser.id)
    }
    LaunchedEffect(cameraPositionState.position.zoom) {
        viewModel.updateMarkerIcons(cameraPositionState.position.zoom, context)
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
            viewModel.markers.forEach { markerData ->
                CreateMapMarker(
                    markerData = markerData,
                    viewModel = viewModel,
                    navController = navController,
                    coroutineScope = coroutineScope,
                    cameraPositionState = cameraPositionState
                )
            }
        }

        ZoomSlider(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            onZoomChange = { newZoom ->
                coroutineScope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.zoomTo(newZoom), 150)
                }
            },
            initialZoom = cameraPositionState.position.zoom
        )
    }
}

@Composable
private fun CreateMapMarker(
    markerData: MarkerData,
    viewModel: MapViewModel,
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    cameraPositionState: CameraPositionState
) {
    markerData.icon?.let {
        Marker(
            state = MarkerState(position = markerData.position),
            icon = it,
            onClick = {
                handleMarkerClick(
                    markerData = markerData,
                    viewModel = viewModel,
                    navController = navController,
                    coroutineScope = coroutineScope,
                    cameraPositionState = cameraPositionState
                )
            }
        )
    }
}

private fun handleMarkerClick(
    markerData: MarkerData,
    viewModel: MapViewModel,
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    cameraPositionState: CameraPositionState
): Boolean {
    return if (viewModel.selectedMarkerId.value == markerData.id) {
        navController.navigate("profile/${markerData.id}")
        viewModel.selectedMarkerId.value = null
        true
    } else {
        coroutineScope.launch {
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(markerData.position), 150)
        }
        viewModel.selectedMarkerId.value = markerData.id
        true
    }
}
