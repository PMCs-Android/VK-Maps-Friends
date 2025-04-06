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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.GeoPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

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
//    val data by viewModel.data.collectAsState()


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
            .padding(vertical = 30.dp, horizontal = 10.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(0.dp)
                .border(4.dp, Color.White, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cross),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        CreateEventTitleInput(title)

        Row(modifier = Modifier.padding(top = 10.dp)) {
            CreateEventDateInput(showDatePicker, date)
            Spacer(modifier = Modifier.width(10.dp))
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
        println(time.value + date.value)
        CreateEventDescriptionInput(descrip)

        CreateEventAddParticipants()

        CreateEventAddLocation()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = {/* Добавление ивента */
                    viewModel.createEvent(
                        title.value,
                        descrip.value,
                        GeoPoint(54.0, 20.0),
                        date.value + " " + time.value,
                        emptyList()
                    )
                },
                modifier = Modifier
                    .border(4.dp, colorResource(R.color.main_blue), RoundedCornerShape(20.dp))
            ) {
                Text(
                    text = "Готово!",
                    fontSize = 20.sp,
                    color = colorResource(R.color.main_blue),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Column {

        }
    }
}

@Composable
fun CreateEventTitleInput(title: MutableState<String>) {
    TextField(
        value = title.value,
        onValueChange = { title.value = it },
        textStyle = TextStyle(fontSize = 20.sp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = 10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Название",
                fontSize = 12.sp,
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
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(start = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selectedDate.value == ""){
            EventDateText()
        }
        else {
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
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(start = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
        ) {
        if (selectedTime.value == ""){
            EventTimeText()
        }
        else {
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
        fontSize = 16.sp,
    )
}

@Composable
fun EventTimeText(time : String = "Время") {
    Text(
        text = time,
        fontSize = 16.sp,
    )
}

@Composable
fun CreateEventDescriptionInput(descrip: MutableState<String>) {
    TextField(
        value = descrip.value,
        onValueChange = { descrip.value = it },
        textStyle = TextStyle(fontSize = 20.sp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
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
                fontSize = 16.sp,
                color = Color.Gray
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            hintLocales = LocaleList(Locale("ru"))
        )
    )
}

// нужно подтянуть друзей из вк
@Composable
fun CreateEventAddParticipants() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .height(48.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(start = 10.dp)
        ) {
            mockEvents[0].members.forEach { member ->
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "MemberIcon",
                    tint = colorResource(R.color.bg_pink),
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            IconButton(
                onClick = {/* Добавить человека */ },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.add_plus),
                    contentDescription = "Add member",
                    tint = colorResource(R.color.main_purple)
                )
            }
        }
    }
}

@Composable
fun CreateEventAddLocation() {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .height(300.dp)
            .background(Color.White, RoundedCornerShape(20.dp))

    ) {
        Text(
            text = mockEvents[0].location,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        )
        Box(
            modifier = Modifier
                .padding(10.dp)
        ) {
            MapScreen()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInput(
    showDatePicker: MutableState<Boolean>,
    state: DatePickerState,
    selectedDate: MutableState<String>
) {
    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker.value = false
            },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        selectedDate.value = "${date.dayOfMonth + 1}.${date.month.ordinal + 1}"
                    }
                    showDatePicker.value = false
                }) {
                    Text("OK")
                }
                TextButton(
                    onClick = { showDatePicker.value = false }
                ) {
                    Text(text = "Cancel")
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                headlineContentColor = colorResource(R.color.main_purple),
                selectedDayContainerColor = colorResource(R.color.main_purple),
                selectedDayContentColor = Color.White,
                todayContentColor = colorResource(R.color.main_purple),
                todayDateBorderColor = colorResource(R.color.main_blue)
            )
        ) {
            DatePicker(state = state)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInput(
    showTimePicker: MutableState<Boolean>,
    state: TimePickerState,
    selectedTime: MutableState<String>
) {
    if (showTimePicker.value) {
        Dialog(
            onDismissRequest = { showTimePicker.value = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                modifier =
                    Modifier
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .background(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surface
                        ),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                        state = state,
                        colors = TimePickerDefaults.colors(
                            containerColor = Color.White,
                            clockDialColor = Color.White,
                            selectorColor = colorResource(R.color.main_blue),
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = Color.Black,
                            timeSelectorSelectedContainerColor = Color.Transparent,
                            timeSelectorUnselectedContainerColor = Color.Transparent,
                            timeSelectorSelectedContentColor = colorResource(R.color.main_blue),
                            timeSelectorUnselectedContentColor = Color.Black
                        )
                    )
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { showTimePicker.value = false }) { Text("Cancel") }
                        TextButton(
                            onClick = {
                                showTimePicker.value = false
                                selectedTime.value =
                                    "${state.hour}:${state.minute}"
                            }
                        ) { Text("OK") }
                    }
                }
            }
        }
    }
}


//@RequiresApi(Build.VERSION_CODES.O)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
////@Preview
//fun DateInputField() {
//    var showDatePicker by remember { mutableStateOf(false) }
//    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
//
//    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        // Поле для отображения выбранной даты
//        OutlinedTextField(
//            value = selectedDate.format(dateFormatter),
//            onValueChange = {},
//            label = { Text("Дата") },
//            modifier = Modifier.fillMaxWidth(),
//            readOnly = true,
//            trailingIcon = {
//                IconButton(onClick = { showDatePicker = true }) {
//                    Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
//                }
//            }
//        )
//    }
//    if (showDatePicker) {
//        val datePickerState = rememberDatePickerState(
//            initialSelectedDateMillis = selectedDate
//                .atStartOfDay(ZoneId.systemDefault())
//                .toInstant()
//                .toEpochMilli()
//        )
//
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                Button(onClick = {
//                    datePickerState.selectedDateMillis?.let {
//                        selectedDate = Instant.ofEpochMilli(it)
//                            .atZone(ZoneId.systemDefault())
//                            .toLocalDate()
//                    }
//                    showDatePicker = false
//                }) {
//                    Text("OK")
//                }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//}

//@Composable
//fun CreateEventDateTimeInput(date: MutableState<String>, time: MutableState<String>) {
//    Row {
//        TextField(
//            value = date.value,
//            onValueChange = { date.value = it },
//            textStyle = TextStyle(fontSize = 20.sp),
//            shape = RoundedCornerShape(20.dp),
//            modifier = Modifier
//                .weight(1f)
//                .height(60.dp)
//                .padding(top = 10.dp),
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = Color.White,
//                unfocusedContainerColor = Color.White,
//                unfocusedIndicatorColor = Color.Transparent,
//                focusedIndicatorColor = Color.Transparent
//            ),
//            placeholder = {
//                Text(
//                    text = "Дата",
//                    fontSize = 12.sp,
//                    color = Color.Gray
//                )
//            },
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Text,
//                hintLocales = LocaleList(Locale("ru"))
//            )
//        )
//        Spacer(modifier = Modifier.width(10.dp))
//        TextField(
//            value = time.value,
//            onValueChange = { time.value = it },
//            textStyle = TextStyle(fontSize = 20.sp),
//            shape = RoundedCornerShape(20.dp),
//            modifier = Modifier
//                .weight(1f)
//                .height(60.dp)
//                .padding(top = 10.dp),
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = Color.White,
//                unfocusedContainerColor = Color.White,
//                unfocusedIndicatorColor = Color.Transparent,
//                focusedIndicatorColor = Color.Transparent
//            ),
//            placeholder = {
//                Text(
//                    text = "Время",
//                    fontSize = 12.sp,
//                    color = Color.Gray
//                )
//            },
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Text,
//                hintLocales = LocaleList(Locale("ru"))
//            )
//        )
//    }
//}