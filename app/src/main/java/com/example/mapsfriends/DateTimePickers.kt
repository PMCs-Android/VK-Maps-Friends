package com.example.mapsfriends

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

object DateTimePickers {
    const val SMALL_PADDING = 6
    const val DEFAULT_PADDING = 24
    const val MEDIUM_FIELD = 40
    const val LARGE_DIALOG = 100
}

@Composable
fun CreateEventDateInput(
    showDatePicker: MutableState<Boolean>,
    selectedDate: MutableState<String>,
    dateError: MutableState<Boolean>
) {
    Column{
        Row(
            modifier = Modifier
                .width(110.dp)
                .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp))
                .padding(start = Dimensions.SMALL_PADDING_1.dp),
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
        if (dateError.value) {
            Text(
                text = "Выберите дату",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun CreateEventTimeInput(
    showTimePicker: MutableState<Boolean>,
    selectedTime: MutableState<String>,
    timeError: MutableState<Boolean>
) {
    Column{
        Row(
            modifier = Modifier
                .width(110.dp)
                .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp))
                .padding(start = Dimensions.SMALL_PADDING_1.dp),
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
        if (timeError.value) {
            Text(
                text = "Выберите время",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun EventDateText(date: String = "Дата") {
    Text(
        text = date,
        fontSize = Dimensions.SMALL_PADDING_3.sp,
    )
}

@Composable
fun EventTimeText(time: String = "Время") {
    Text(
        text = time,
        fontSize = Dimensions.SMALL_PADDING_3.sp,
    )
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
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let {
                            val date = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            selectedDate.value = "%02d.%02d".format(
                                date.dayOfMonth,
                                date.monthValue
                            )
                        }
                        showDatePicker.value = false
                    }
                ) {
                    Text("OK", color = colorResource(R.color.main_purple))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker.value = false }
                ) {
                    Text("Отмена", color = colorResource(R.color.main_purple))
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
    AnimatedVisibility(
        visible = showTimePicker.value,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        if (showTimePicker.value) {
            Dialog(
                onDismissRequest = { showTimePicker.value = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = DateTimePickers.SMALL_PADDING.dp,
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .background(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surface
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(DateTimePickers.DEFAULT_PADDING.dp),
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
                                .height(DateTimePickers.MEDIUM_FIELD.dp)
                                .fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            TimePickerButtons(showTimePicker, state, selectedTime)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerButtons(
    showTimePicker: MutableState<Boolean>,
    state: TimePickerState,
    selectedTime: MutableState<String>
) {
    TextButton(
        onClick = {
            showTimePicker.value = false
        }
    ) { Text("Отмена") }
    TextButton(
        onClick = {
            showTimePicker.value = false
            selectedTime.value = "${state.hour}:${state.minute}"
            if (state.hour.toString().length < 2 &&
                state.minute.toString().length < 2
            ) {
                selectedTime.value = "0${state.hour}:0${state.minute}"
            } else if (state.hour.toString().length < 2 &&
                state.minute.toString().length == 2
            ) {
                selectedTime.value = "0${state.hour}:${state.minute}"
            } else if (state.hour.toString().length == 2 &&
                state.minute.toString().length < 2
            ) {
                selectedTime.value = "${state.hour}:0${state.minute}"
            } else {
                selectedTime.value = "${state.hour}:${state.minute}"
            }
        }
    ) { Text("OK") }
}

fun getWeekdayFromDate(day: Int, month: Int): String {
    val date = LocalDate.of(LocalDate.now().year, month, day)
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).lowercase()
}

