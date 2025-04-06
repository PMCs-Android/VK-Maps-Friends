package com.example.mapsfriends

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

data class MarkerData(
    val position: LatLng,
    val title: String,
    val originalBitmap: Bitmap,
    var icon: BitmapDescriptor? = null
)

@Composable
fun MainScreen(
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 30.dp)
    ) {

        MapScreen()

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { navController.navigate("profile") },
                modifier = Modifier
                    .background(colorResource(R.color.white), RoundedCornerShape(12.dp))
                    .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.profile),
                    contentDescription = "Profile",
                    tint = colorResource(R.color.main_purple)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            IconButton(
                onClick = { /* Показать статистику шагов */ },
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.footstep_boot),
                    contentDescription = "Footsteps",
                    tint = colorResource(R.color.main_purple)
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp)
        ) {
            IconButton(
                onClick = { /* Центрировать геоположение */ },
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.gps_focus),
                    contentDescription = "Location",
                    tint = colorResource(R.color.main_purple)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                IconButton(
                    onClick = { /* Показать чаты */ },
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(4.dp, colorResource(R.color.main_blue), RoundedCornerShape(12.dp))
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.chat_main_page),
                        contentDescription = "Chats",
                        tint = colorResource(R.color.main_blue)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = { navController.navigate("events") },
                    modifier = Modifier
                        .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 16.dp)
                        .height(56.dp)
                        .width(56.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.event_calendar),
                        contentDescription = "Events",
                        tint = colorResource(R.color.main_purple)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = { /* Показать друзей */ },
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(4.dp, colorResource(R.color.main_pink), RoundedCornerShape(12.dp))
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.friends),
                        contentDescription = "Friends",
                        tint = colorResource(R.color.main_pink)
                    )
                }
            }
        }
    }
}

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentUser = mockUsers.firstOrNull { it.id == 1 } ?: mockUsers.first()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentUser.location, 18f)
    }

    var zoomLevel by remember { mutableFloatStateOf(18f) }

    val markers = remember { mutableStateListOf<MarkerData>() }

    LaunchedEffect(Unit) {
        mockUsers.forEach { user ->
            coroutineScope.launch {
                val originalBitmap = loadOriginalBitmapFromUrl(context, user.avatarUrl)
                if (originalBitmap != null) {
                    val initialZoom = calculateMarkerSize(zoomLevel)
                    val initialIcon = BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(originalBitmap, initialZoom, initialZoom, false)
                    )
                    markers.add(
                        MarkerData(
                            position = user.location,
                            title = user.name,
                            originalBitmap = originalBitmap,
                            icon = initialIcon
                        )
                    )
                }
            }
        }
    }

    LaunchedEffect(cameraPositionState.position.zoom) {
        zoomLevel = cameraPositionState.position.zoom.coerceIn(5f, 20f)
        markers.forEach { markerData ->
            val newSize = calculateMarkerSize(zoomLevel)
            markerData.icon = BitmapDescriptorFactory.fromBitmap(
                Bitmap.createScaledBitmap(markerData.originalBitmap, newSize, newSize, false)
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

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxSize()
        ) {
            Slider(
                value = zoomLevel,
                onValueChange = { newZoom ->
                    zoomLevel = newZoom.coerceIn(5f, 20f)
                    cameraPositionState.position = CameraPosition
                        .Builder(cameraPositionState.position)
                        .zoom(newZoom)
                        .build()
                },
                valueRange = 5f..20f,
                steps = 50,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Blue,
                    inactiveTrackColor = Color.LightGray
                ),
                modifier = Modifier
                    .height(300.dp)
                    .width(48.dp)
                    .rotate(90f),
            )
        }
    }
}

