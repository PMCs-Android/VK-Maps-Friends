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
    DisposableEffect(Unit) {
        onDispose {
            viewModel.tryDeleteIncompleteEvent()
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
            .padding(
                vertical = Dimensions.MEDIUM_SPACING_2.dp,
                horizontal = Dimensions.SMALL_PADDING_1.dp
            )
    ) {
        ExitButton(navController)
        CreateEventTitleInput(viewModel, currentEvent)
        Row(modifier = Modifier.padding(top = Dimensions.SMALL_PADDING_1.dp)) {
            CreateEventDateInput(showDatePicker, date)
            Spacer(modifier = Modifier.width(Dimensions.SMALL_PADDING_1.dp))
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
                .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp))
                .padding(Dimensions.SMALL_PADDING_1.dp),
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
            .border(
                Dimensions.BORDER_WIDTH.dp,
                Color.White,
                RoundedCornerShape(Dimensions.SMALL_PADDING_2.dp)
            )
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
        textStyle = TextStyle(fontSize = Dimensions.MEDIUM_SPACING_1.sp),
        shape = RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.MEDIUM_SPACING_5.dp)
            .padding(top = Dimensions.SMALL_PADDING_1.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Название",
                fontSize = Dimensions.SMALL_PADDING_3.sp,
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
        textStyle = TextStyle(fontSize = Dimensions.MEDIUM_SPACING_1.sp),
        shape = RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.SMALL_PADDING_1.dp)
            .height(Dimensions.LARGE_ELEMENT_1.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Описание",
                fontSize = Dimensions.SMALL_PADDING_3.sp,
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
                        .clip(CircleShape)
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

@Composable
fun CreateEventAddLocation() {
    Column(
        modifier = Modifier
            .padding(vertical = Dimensions.SMALL_PADDING_1.dp)
            .height(Dimensions.LARGE_ELEMENT_2.dp)
            .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp))

    ) {
        Text(
            text = mockEvents[0].location,
            fontSize = Dimensions.SMALL_PADDING_3.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(
                    start = Dimensions.SMALL_PADDING_1.dp,
                    top = Dimensions.SMALL_PADDING_1.dp
                )
        )
        Box(
            modifier = Modifier.padding(Dimensions.SMALL_PADDING_1.dp)
        ) {
            // MapScreen()
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
