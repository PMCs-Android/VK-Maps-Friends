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

object Dimensions {
    const val BORDER = 4
    const val SMALL1 = 10
    const val SMALL2 = 12
    const val SMALL3 = 16
    const val MEDIUM1 = 20
    const val MEDIUM2 = 30
    const val MEDIUM3 = 36
    const val MEDIUM4 = 48
    const val MEDIUM5 = 68
    const val LARGE1 = 80
    const val LARGE2 = 300
    const val LARGE3 = 400
    const val LARGE4 = 800
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
            .padding(vertical = Dimensions.MEDIUM2.dp, horizontal = Dimensions.SMALL1.dp)
    ) {
        ExitButton(navController)
        CreateEventTitleInput(viewModel, currentEvent)
        Row(modifier = Modifier.padding(top = Dimensions.SMALL1.dp)) {
            CreateEventDateInput(showDatePicker, date)
            Spacer(modifier = Modifier.width(Dimensions.SMALL1.dp))
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
                .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM1.dp))
                .padding(Dimensions.SMALL1.dp),
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
            .border(Dimensions.BORDER.dp, Color.White, RoundedCornerShape(Dimensions.SMALL2.dp))
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
        textStyle = TextStyle(fontSize = Dimensions.MEDIUM1.sp),
        shape = RoundedCornerShape(Dimensions.MEDIUM1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.MEDIUM5.dp)
            .padding(top = Dimensions.SMALL1.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Название",
                fontSize = Dimensions.SMALL3.sp,
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
        textStyle = TextStyle(fontSize = Dimensions.MEDIUM1.sp),
        shape = RoundedCornerShape(Dimensions.MEDIUM1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.SMALL1.dp)
            .height(Dimensions.LARGE1.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Описание",
                fontSize = Dimensions.SMALL3.sp,
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
                .height(Dimensions.MEDIUM4.dp)
                .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM1.dp))
                .padding(start = Dimensions.SMALL1.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically

        ) {
            participants.forEach { participant ->
                AsyncImage(
                    model = participant.avatarUrl,
                    contentDescription = "Friend Avatar",
                    modifier = Modifier
                        .size(Dimensions.MEDIUM3.dp)
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
            .padding(vertical = Dimensions.SMALL1.dp)
            .height(Dimensions.LARGE2.dp)
            .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM1.dp))

    ) {
        Text(
            text = mockEvents[0].location,
            fontSize = Dimensions.SMALL3.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = Dimensions.SMALL1.dp, top = Dimensions.SMALL1.dp)
        )
        Box(
            modifier = Modifier.padding(Dimensions.SMALL1.dp)
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
            Dimensions.BORDER.dp,
            colorResource(R.color.main_blue),
            RoundedCornerShape(Dimensions.MEDIUM1.dp)
        )
    ) {
        Text(
            text = "Готово!",
            fontSize = Dimensions.MEDIUM1.sp,
            color = colorResource(R.color.main_blue),
            fontWeight = FontWeight.Bold,
        )
    }
}
