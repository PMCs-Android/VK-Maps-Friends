package com.example.mapsfriends

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.mapsfriends.login.AuthViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    viewModel: EventViewModel = hiltViewModel()
) {
    val date = remember { mutableStateOf("") }
    val time = remember { mutableStateOf("") }
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val dateError = viewModel.dateError
    val timeError = viewModel.timeError
    val state = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(
        initialHour = LocalDateTime.now().hour,
        initialMinute = LocalDateTime.now().minute,
        is24Hour = true,
    )
    val currentEvent by viewModel.currentEvent.collectAsState()
    val showAddFriend = remember { mutableStateOf(false) }
    val creatorId = authViewModel.getCurrentUserId()!!

    LaunchedEffect(Unit) {
        if (currentEvent == null) viewModel.createNewEvent()
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.tryDeleteIncompleteEvent() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(colorResource(R.color.bg_blue), colorResource(R.color.bg_pink))
                )
            )
            .padding(
                vertical = Dimensions.MEDIUM_SPACING_2.dp,
                horizontal = Dimensions.SMALL_PADDING_1.dp
            )
    ) {
        CreateEventHeader(navController, viewModel, currentEvent?.eventId ?: "", creatorId)
        CreateEventTitleInput(viewModel, currentEvent)
        Row(modifier = Modifier.padding(top = Dimensions.SMALL_PADDING_1.dp)) {
            CreateEventDateInput(showDatePicker, date, dateError)
            Spacer(modifier = Modifier.width(Dimensions.SMALL_PADDING_1.dp))
            CreateEventTimeInput(showTimePicker, time, timeError)
        }
        DateInput(showDatePicker, state, date)
        TimeInput(showTimePicker, timePickerState, time)
        CreateEventDescriptionInput(viewModel, currentEvent)
        CreateEventAddParticipants(showAddFriend, viewModel, creatorId)
        CreateEventAddLocation(viewModel)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp))
                .padding(Dimensions.SMALL_PADDING_1.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CreateEventDoneButton(viewModel, navController, date.value + " " + time.value)
        }
    }
}

@Composable
fun CreateEventHeader(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    eventId: String,
    creatorId: String
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {
                eventViewModel.deleteEvent(eventId, creatorId)
                navController.popBackStack()
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cross),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = "Создание ивента",
            fontSize = DateTimePickers.DEFAULT_PADDING.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun CreateEventTitleInput(viewModel: EventViewModel, event: Event?) {
    val isError = viewModel.titleError.value
    TextField(
        value = event?.title ?: "",
        onValueChange = { viewModel.setEventTitle(it) },
        textStyle = TextStyle(fontSize = Dimensions.MEDIUM_SPACING_1.sp),
        shape = RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.LARGE_ELEMENT_1.dp)
            .padding(top = Dimensions.SMALL_PADDING_1.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            errorContainerColor = Color.White,
            errorIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Название",
                fontSize = Dimensions.SMALL_PADDING_3.sp,
                color = Color.Gray
            )
        },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    "Это поле обязательно для заполнения",
                    color = Color.Red
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            hintLocales = LocaleList(Locale("ru"))
        )
    )
}

@Composable
fun CreateEventDescriptionInput(viewModel: EventViewModel, event: Event?) {
    val isError = viewModel.descriptionError.value
    TextField(
        value = event?.description ?: "",
        onValueChange = { viewModel.setEventDescription(it) },
        textStyle = TextStyle(fontSize = Dimensions.MEDIUM_SPACING_1.sp),
        shape = RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.SMALL_PADDING_1.dp)
            .height(Dimensions.PROFILE_ICON_SIZE.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            errorContainerColor = Color.White,
            errorIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Описание",
                fontSize = Dimensions.SMALL_PADDING_3.sp,
                color = Color.Gray
            )
        },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    "Это поле обязательно для заполнения",
                    color = Color.Red
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            hintLocales = LocaleList(Locale("ru"))
        )
    )
}

@Composable
fun CreateEventAddParticipants(
    showAddFriend: MutableState<Boolean>,
    viewModel: EventViewModel,
    creatorId: String
) {
    val participants by viewModel.participants.collectAsState()
    val createdEventId = viewModel.currentEvent.collectAsState().value?.eventId

//    LaunchedEffect(showAddFriend.value) {
//        if (!showAddFriend.value) {
//            viewModel.loadParticipants(createdEventId.toString())
//        }
//    }
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .height(Dimensions.MEDIUM_SPACING_4.dp)
                .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp))
                .padding(start = Dimensions.SMALL_PADDING_1.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically

        ) {
            participants.forEach { participant ->
                AsyncImage(
                    model = participant.avatarUrl,
                    contentDescription = "Friend Avatar",
                    modifier = Modifier
                        .size(Dimensions.MEDIUM_SPACING_3.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds
                )
            }
            IconButton(
                onClick = {
                    showAddFriend.value = true
                    // viewModel.saveCurrentEvent()
                },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.add_plus),
                    contentDescription = "Add participants",
                    tint = colorResource(R.color.main_purple)
                )
            }
        }
    }
    if (showAddFriend.value) {
        AddParticipantsScreen(
            viewModel,
            showAddFriend
        )
    }
}

@Suppress("LongMethod")
@Composable
fun CreateEventAddLocation(
    viewModel: EventViewModel,
    mapViewModel: MapViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val selectedLocation = remember { mutableStateOf<GeoPoint?>(null) }
    val addressText = remember { mutableStateOf<String?>(null) } // Состояние для адреса
    val context = LocalContext.current
    val defaultGeo = GeoPoint(Dimensions.GEO5, Dimensions.GEO6)
    val currentUser = userViewModel.currentUser.collectAsState().value
    val coroutineScope = rememberCoroutineScope() // Для вызова suspend функций

    if (isValidUser(currentUser)) {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                mapViewModel.convertToLatLng(currentUser!!.location),
                Dimensions.ZOOM
            )
        }

        Column(
            modifier = Modifier
                .padding(vertical = Dimensions.SMALL_PADDING_1.dp)
                .height(Dimensions.LARGE_ELEMENT_2.dp)
                .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GoogleMap(
                    properties = MapProperties(
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.map_style
                        )
                    ),
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        compassEnabled = false
                    ),
                    onMapClick = { latLng ->
                        selectedLocation.value = GeoPoint(latLng.latitude, latLng.longitude)
                        viewModel.setEventLocation(selectedLocation.value ?: defaultGeo)

                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                addressText.value = mapViewModel.getOSMAddress(
                                    latLng.latitude,
                                    latLng.longitude
                                )
                            }
                        }
                    }
                ) {
                    selectedLocation.value?.let { location ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(location.latitude, location.longitude)
                            ),
                            title = "Место события"
                        )
                    }
                }
            }
            Text(
                text = addressText.value ?: "Нажмите на карту для выбора места",
                fontSize = Dimensions.SMALL_PADDING_3.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(all = Dimensions.SMALL_PADDING_1.dp)
            )
//        }
        }
    }
}

@Composable
fun CreateEventDoneButton(
    viewModel: EventViewModel,
    navController: NavHostController,
    datetime: String
) {
    viewModel.setEventTime(datetime)
    TextButton(
        onClick = {
            if (viewModel.validateFields()) {
                viewModel.saveCurrentEvent()
                navController.navigate("events")
            }
        },
        modifier = Modifier.border(
            Dimensions.BORDER_WIDTH.dp,
            colorResource(R.color.main_blue),
            RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp)
        )
    ) {
        Text(
            text = "Готово!",
            fontSize = Dimensions.MEDIUM_SPACING_1.sp,
            color = colorResource(R.color.main_blue),
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun isValidUser(currentUser: User?): Boolean {
    return currentUser != null &&
        currentUser.location.latitude != 0.0 &&
        currentUser.location.longitude != 0.0
}
