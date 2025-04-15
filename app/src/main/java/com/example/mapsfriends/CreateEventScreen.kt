package com.example.mapsfriends

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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

object Constants {
    const val BORDER = 4
    const val SMALL1 = 10
    const val SMALL2 = 12
    const val MEDIUM1 = 16
    const val MEDIUM2 = 20
    const val LARGE = 30
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
            .padding(vertical = Constants.LARGE.dp, horizontal = Constants.SMALL1.dp)
    ) {
        ExitButton(navController)
        CreateEventTitleInput(viewModel, currentEvent)
        Row(modifier = Modifier.padding(top = Constants.SMALL1.dp)) {
            CreateEventDateInput(showDatePicker, date)
            Spacer(modifier = Modifier.width(Constants.SMALL1.dp))
            CreateEventTimeInput(showTimePicker, time)
        }
        AnimatedVisibility(
            visible = showDatePicker.value,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            DateInput(showDatePicker, state, date)
        }
        AnimatedVisibility(
            visible = showTimePicker.value,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            TimeInput(showTimePicker, timePickerState, time)
        }
        viewModel.setEventTime(date.value + " " + time.value)
        CreateEventDescriptionInput(viewModel, currentEvent)
        CreateEventAddParticipants(showAddFriend, viewModel)
        CreateEventAddLocation()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(Constants.MEDIUM2.dp))
                .padding(Constants.SMALL1.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = {
                    viewModel.saveCurrentEvent()
                    navController.navigate("events")
                },
                modifier = Modifier.border(
                    4.dp,
                    colorResource(R.color.main_blue),
                    RoundedCornerShape(Constants.MEDIUM2.dp)
                )
            ) {
                Text(
                    text = "Готово! ${viewModel.events.collectAsState().value.size}",
                    fontSize = Constants.MEDIUM2.sp,
                    color = colorResource(R.color.main_blue),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun ExitButton(navController: NavHostController) {
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = Modifier
            .padding(0.dp)
            .border(Constants.BORDER.dp, Color.White, RoundedCornerShape(Constants.SMALL2.dp))
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
        textStyle = TextStyle(fontSize = Constants.MEDIUM2.sp),
        shape = RoundedCornerShape(Constants.MEDIUM2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(top = Constants.SMALL1.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Название",
                fontSize = Constants.MEDIUM1.sp,
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
fun CreateEventDateInput(
    showDatePicker: MutableState<Boolean>,
    selectedDate: MutableState<String>,
) {
    Row(
        modifier = Modifier
            .width(100.dp)
            .background(Color.White, RoundedCornerShape(Constants.MEDIUM2.dp))
            .padding(start = Constants.SMALL1.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selectedDate.value == "") {
            EventDateText()
        } else {
            EventDateText(selectedDate.value)
        }
        IconButton(
            onClick = { showDatePicker.value = true }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.calendar),
                contentDescription = "Date Picker",
                tint = colorResource(R.color.main_blue),
            )
        }
    }
}

@Composable
fun CreateEventTimeInput(
    showTimePicker: MutableState<Boolean>,
    selectedTime: MutableState<String>
) {
    Row(
        modifier = Modifier
            .width(100.dp)
            .background(Color.White, RoundedCornerShape(Constants.MEDIUM2.dp))
            .padding(start = Constants.SMALL1.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selectedTime.value == "") {
            EventTimeText()
        } else {
            EventTimeText(selectedTime.value)
        }
        IconButton(
            onClick = { showTimePicker.value = true }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.clock),
                contentDescription = "Time Picker",
                tint = colorResource(R.color.main_blue)
            )
        }
    }
}

@Composable
fun EventDateText(date: String = "Дата") {
    Text(
        text = date,
        fontSize = Constants.MEDIUM1.sp,
    )
}

@Composable
fun EventTimeText(time: String = "Время") {
    Text(
        text = time,
        fontSize = Constants.MEDIUM1.sp,
    )
}

@Composable
fun CreateEventDescriptionInput(viewModel: EventViewModel, event: Event?) {
    TextField(
        value = event?.description ?: "",
        onValueChange = { viewModel.setEventDescription(it) },
        textStyle = TextStyle(fontSize = Constants.MEDIUM2.sp),
        shape = RoundedCornerShape(Constants.MEDIUM2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Constants.SMALL1.dp)
            .height(80.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Описание",
                fontSize = Constants.MEDIUM1.sp,
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
                .height(48.dp)
                .background(Color.White, RoundedCornerShape(Constants.MEDIUM2.dp))
                .padding(start = Constants.SMALL1.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically

        ) {
            participants.forEach { participant ->
                AsyncImage(
                    model = participant.avatarUrl,
                    contentDescription = "Friend Avatar",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
            }
            IconButton(
                onClick = {
                    showAddFriend.value = true
                    viewModel.saveCurrentEvent()
                },
                modifier = Modifier
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
            .padding(vertical = Constants.SMALL1.dp)
            .height(300.dp)
            .background(Color.White, RoundedCornerShape(Constants.MEDIUM2.dp))

    ) {
        Text(
            text = mockEvents[0].location,
            fontSize = Constants.MEDIUM1.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = Constants.SMALL1.dp, top = Constants.SMALL1.dp)
        )
        Box(
            modifier = Modifier.padding(Constants.SMALL1.dp)
        ) {
            MapScreen()
        }
    }
}
