package com.example.mapsfriends

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.platform.LocalContext
import androidx.room.util.copy
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch
import com.google.maps.android.compose.Marker as Marker


@Composable
fun MainScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Карта
        MapScreen()

        // Кнопки поверх карты
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { /* Центрировать на местоположении */ },
                modifier = Modifier.background(Color.White, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location")
            }

            Spacer(modifier = Modifier.height(8.dp))

            IconButton(
                onClick = { /* Показать друзей */ },
                modifier = Modifier.background(Color.White, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Face, contentDescription = "Friends")
            }
        }
    }
}


@Composable
fun MapScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Текущий пользователь
    val currentUser = mockUsers.firstOrNull { it.id == 1 } ?: mockUsers.first()

    // Состояние камеры
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentUser.location, 18f)
    }

    var zoomLevel by remember { mutableFloatStateOf(18f) }

    val markers = remember { mutableStateListOf<MarkerData>() }

    LaunchedEffect(Unit) {
        mockUsers.forEach { user ->
            coroutineScope.launch {
                val markerIcon = loadMarkerIconFromUrl(context, user.avatarUrl)
                if (markerIcon != null) {
                    markers.add(
                        MarkerData(
                            position = user.location,
                            title = user.name,
                            icon = markerIcon
                        )
                    )
                }
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // Карта
        GoogleMap(
            properties = MapProperties(
                //mapToolbarEnabled = false, // Отключаем гугловский элемент UI для маршрутов
                //isLiteModeEnabled = false,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            ),
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false, // Скрываем кнопки "+" и "-"
                myLocationButtonEnabled = false, // Скрываем кнопку "Мое местоположение"
                compassEnabled = false // Скрываем компас
            )
        ) {
            // Отображение маркеров
            markers.forEach { markerData ->
                Marker(
                    state = MarkerState(position = markerData.position),
                    title = markerData.title,
                    icon = markerData.icon,
                    onClick = { false }
                )
            }
        }

        // Слайдер для управления зумом
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxSize()
        ) {
            Slider(
                value = zoomLevel,
                onValueChange = { newZoom ->
                    zoomLevel = newZoom.coerceIn(5f, 20f) // Ограничение зума от 5 до 20
                    cameraPositionState.position = CameraPosition.Builder(cameraPositionState.position)
                        .zoom(newZoom) // Устанавливаем новый уровень зума
                        .build()
                },
                valueRange = 5f..20f, // Диапазон зума
                steps = 100, // Более плавный слайдер (больше шагов)
                colors = SliderDefaults.colors(
                    thumbColor = Color.White, // Цвет индикатора
                    activeTrackColor = Color.Blue, // Активная часть слайдера
                    inactiveTrackColor = Color.LightGray // Неактивная часть слайдера
                ),
                modifier = Modifier
                    .height(300.dp)
                    .width(48.dp)
                    .rotate(90f),
            )
        }
    }
}


data class MarkerData(
    val position: LatLng,
    val title: String,
    val icon: BitmapDescriptor
)


//val animatedOffset = animateFloatAsState(
//    targetValue = if (isPulsing) 1.2f else 1f,
//    animationSpec = infiniteRepeatable(
//        animation = tween(durationMillis = 1000),
//        repeatMode = RepeatMode.Reverse
//    )
//)

@Preview
@Composable
fun SimpleComposablePreview() {
    MainScreen()
}