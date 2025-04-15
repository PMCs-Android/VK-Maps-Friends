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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import java.time.LocalDateTime

object UiDimensions {
    const val BORDER_SIZE = 4
    const val SMALL1_SIZE = 10
    const val SMALL2_SIZE = 12
    const val SMALL3_SIZE = 16
    const val MEDIUM1_SIZE = 20
    const val MEDIUM2_SIZE = 30
    const val MEDIUM3_SIZE = 36
    const val MEDIUM4_SIZE = 48
    const val MEDIUM5_SIZE = 68
    const val LARGE1_SIZE = 80
    const val LARGE2_SIZE = 300
    const val LARGE3_SIZE = 400
    const val LARGE4_SIZE = 800
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(navController: NavHostController) {
    val date = remember { mutableStateOf("") }
    val time = remember { mutableStateOf("") }
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val state = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(
        initialHour = LocalDateTime.now().hour,
        initialMinute = LocalDateTime.now().minute,
        is24Hour = true,
    )
    val viewModel = hiltViewModel<EventViewModel>()
    val currentEvent by viewModel.currentEvent.collectAsState()
    val showAddFriend = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (currentEvent == null) {
            viewModel.createNewEvent()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(R.color.bg_blue),
                        colorResource(R.color.bg_pink)
                    )
                )
            )
            .padding(vertical = UiDimensions.MEDIUM2_SIZE.dp, horizontal = UiDimensions.SMALL1_SIZE.dp)
    ) {
        ExitButton(navController)
        CreateEventTitleInput(viewModel, currentEvent)
        Row(modifier = Modifier.padding(top = UiDimensions.SMALL1_SIZE.dp)) {
            CreateEventDateInput(showDatePicker, date)
            Spacer(modifier = Modifier.width(UiDimensions.SMALL1_SIZE.dp))
            CreateEventTimeInput(showTimePicker, time)
        }
        DateInput(showDatePicker, state, date)
        TimeInput(showTimePicker, timePickerState, time)
        viewModel.setEventTime(date.value + " " + time.value)

        CreateEventDescriptionInput(viewModel, currentEvent)
        CreateEventAddParticipants(showAddFriend, viewModel)
        CreateEventAddLocation()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(UiDimensions.MEDIUM1_SIZE.dp))
                .padding(UiDimensions.SMALL1_SIZE.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CreateEventDoneButton(viewModel, navController)
        }
    }
}

@Composable
fun ExitButton(navController: NavHostController) {
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = Modifier
            .border(UiDimensions.BORDER_SIZE.dp, Color.White, RoundedCornerShape(UiDimensions.SMALL2_SIZE.dp))
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.cross),
            contentDescription = "Back",
            tint = Color.White
        )
    }
}

@Composable
fun CreateEventTitleInput(viewModel: EventViewModel, event: Event?) {
    TextField(
        value = event?.title ?: "",
        onValueChange = { viewModel.setEventTitle(it) },
        textStyle = TextStyle(fontSize = UiDimensions.MEDIUM1_SIZE.sp),
        shape = RoundedCornerShape(UiDimensions.MEDIUM1_SIZE.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(UiDimensions.MEDIUM5_SIZE.dp)
            .padding(top = UiDimensions.SMALL1_SIZE.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Название",
                fontSize = UiDimensions.SMALL3_SIZE.sp,
                color = Color.Gray
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            hintLocales = LocaleList(Locale("ru"))
        )
    )
}

@Composable
fun CreateEventDescriptionInput(viewModel: EventViewModel, event: Event?) {
    TextField(
        value = event?.description ?: "",
        onValueChange = { viewModel.setEventDescription(it) },
        textStyle = TextStyle(fontSize = UiDimensions.MEDIUM1_SIZE.sp),
        shape = RoundedCornerShape(UiDimensions.MEDIUM1_SIZE.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = UiDimensions.SMALL1_SIZE.dp)
            .height(UiDimensions.LARGE1_SIZE.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Описание",
                fontSize = UiDimensions.SMALL3_SIZE.sp,
                color = Color.Gray
            )
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
    viewModel: EventViewModel
) {
    val participants by viewModel.participants.collectAsState()
    val createdEventId = viewModel.currentEvent.collectAsState().value?.eventId

    LaunchedEffect(showAddFriend.value) {
        if (!showAddFriend.value) {
            viewModel.loadParticipants(createdEventId.toString())
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .height(UiDimensions.MEDIUM4_SIZE.dp)
                .background(Color.White, RoundedCornerShape(UiDimensions.MEDIUM1_SIZE.dp))
                .padding(start = UiDimensions.SMALL1_SIZE.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically

        ) {
            participants.forEach { participant ->
                AsyncImage(
                    model = participant.avatarUrl,
                    contentDescription = "Friend Avatar",
                    modifier = Modifier
                        .size(UiDimensions.MEDIUM3_SIZE.dp)
                        .clip(CircleShape)
                )
            }
            IconButton(
                onClick = {
                    showAddFriend.value = true
                    viewModel.saveCurrentEvent()
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

@Composable
fun CreateEventAddLocation() {
    Column(
        modifier = Modifier
            .padding(vertical = UiDimensions.SMALL1_SIZE.dp)
            .height(UiDimensions.LARGE2_SIZE.dp)
            .background(Color.White, RoundedCornerShape(UiDimensions.MEDIUM1_SIZE.dp))

    ) {
        Text(
            text = mockEvents[0].location,
            fontSize = UiDimensions.SMALL3_SIZE.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = UiDimensions.SMALL1_SIZE.dp, top = UiDimensions.SMALL1_SIZE.dp)
        )
        Box(
            modifier = Modifier.padding(UiDimensions.SMALL1_SIZE.dp)
        ) {
            MapScreen()
        }
    }
}

@Composable
fun CreateEventDoneButton(viewModel: EventViewModel, navController: NavHostController) {
    TextButton(
        onClick = {
            viewModel.saveCurrentEvent()
            navController.navigate("events")
        },
        modifier = Modifier.border(
            UiDimensions.BORDER_SIZE.dp,
            colorResource(R.color.main_blue),
            RoundedCornerShape(UiDimensions.MEDIUM1_SIZE.dp)
        )
    ) {
        Text(
            text = "Готово!",
            fontSize = UiDimensions.MEDIUM1_SIZE.sp,
            color = colorResource(R.color.main_blue),
            fontWeight = FontWeight.Bold,
        )
    }
}
