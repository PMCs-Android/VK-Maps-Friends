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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

object Constants {
    const val BORDER = 4
    const val SMALL1 = 10
    const val SMALL2 = 12
    const val MEDIUM1 = 16
    const val MEDIUM2 = 20
    const val LARGE = 30
}

val currentUser = User(
    userId = "111",
    username = "test user",
    avatarUrl = "https://avatars.mds.yandex.net/get-mpic/5346238/img_id1357746595382532818.jpeg/orig",
    friends = listOf("112", "113"),
    location = GeoPoint(55.751244, 37.618423)
)

val friend1 = User(
    userId = "112",
    username = "test user2",
    avatarUrl = "https://avatars.mds.yandex.net/get-entity_search/1969011/918366713/orig",
    friends = listOf(),
    location = GeoPoint(55.740000, 37.600000)
)

val friend2 = User(
    userId = "113",
    username = "test user3",
    avatarUrl = "https://i.pinimg.com/736x/be/46/46/be4646a42ab267d93f035f93752eb796.jpg",
    friends = listOf(),
    location = GeoPoint(55.760000, 37.620000)
)

val event1 = Event(
    "1",
    "111",
    "gegege",
    "aaaaaaaaaaaa",
    GeoPoint(50.004, 23.004),
    "13.4.25 12:10",
    listOf("111", "112", "113")
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(navController: NavHostController) {
    val title = remember { mutableStateOf("") }
    val descrip = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    val time = remember { mutableStateOf("") }
//    val location = remember { mutableStateOf(GeoPoint(0.0, 0.0)) }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val state = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(
        initialHour = LocalDateTime.now().hour,
        initialMinute = LocalDateTime.now().minute,
        is24Hour = true,
    )

    val viewModel = hiltViewModel<EventViewModel>()

//    LaunchedEffect(Unit) {
//        if (viewModel.eventId.value == null) {
//            viewModel.generateNewEventId()
//        }
//    }
    val createdEventId = viewModel.eventId

    val showAddFriend = remember { mutableStateOf(false) }

    viewModel.createEvent(
        "1",
        "gegege",
        "aaaaaaaaaaaa",
        GeoPoint(50.004, 23.004),
        "13.4.25 12:10",
        listOf("111", "112", "113")
    )

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
        CreateEventTitleInput(title)
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
        CreateEventDescriptionInput(descrip)

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
                onClick = { /* Добавление ивента */
                    viewModel.createEvent(
                        createdEventId.toString(),
                        title.value,
                        descrip.value,
                        GeoPoint(54.0, 20.0),
                        date.value + " " + time.value,
                        viewModel.participants.value.map { it.userId }
                    )
                    navController.navigate("events")
//                    viewModel.resetEventId()
                },
                modifier = Modifier.border(
                    4.dp,
                    colorResource(R.color.main_blue),
                    RoundedCornerShape(Constants.MEDIUM2.dp)
                )
            ) {
                Text(
                    text = "Готово!",
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
fun CreateEventTitleInput(title: MutableState<String>) {
    TextField(
        value = title.value,
        onValueChange = { title.value = it },
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
fun CreateEventDescriptionInput(descrip: MutableState<String>) {
    TextField(
        value = descrip.value,
        onValueChange = { descrip.value = it },
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
//    val viewModel = hiltViewModel<EventViewModel>()
    val participants by viewModel.participants.collectAsState()
    val createdEventId = viewModel.eventId

    // Автоматическое обновление при изменении состояния showAddFriend
    LaunchedEffect(showAddFriend.value) {
        if (!showAddFriend.value) {
            viewModel.loadParticipants()
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
//            Text(
//                text = createdEventId
//            )
            participants.forEach { participant ->
//                AsyncImage(
//                    model = participant.avatarUrl,
//                    contentDescription = "Friend Avatar",
//                    modifier = Modifier
//                        .size(36.dp)
//                )
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = ""
                )
            }
            IconButton(
                onClick = { showAddFriend.value = true },
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
            viewModel, showAddFriend
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
