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
import java.time.ZoneId

object PickersConstants {
    const val SMALL1 = 6
    const val SMALL2 = 24
    const val MEDIUM = 40
    const val LARGE = 100
}

@Composable
fun CreateEventDateInput(
    showDatePicker: MutableState<Boolean>,
    selectedDate: MutableState<String>,
) {
    Row(
        modifier = Modifier
            .width(PickersConstants.LARGE.dp)
            .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM1.dp))
            .padding(start = Dimensions.SMALL1.dp),
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
            .width(PickersConstants.LARGE.dp)
            .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM1.dp))
            .padding(start = Dimensions.SMALL1.dp),
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
        fontSize = Dimensions.SMALL3.sp,
    )
}

@Composable
fun EventTimeText(time: String = "Время") {
    Text(
        text = time,
        fontSize = Dimensions.SMALL3.sp,
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
    AnimatedVisibility(
        visible = showDatePicker.value,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
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
                                val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                selectedDate.value = "${date.dayOfMonth}.${date.month.value}"
                            }
                            showDatePicker.value = false
                        }
                    ) {
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
                    tonalElevation = PickersConstants.SMALL1.dp,
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .background(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surface
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(PickersConstants.SMALL2.dp),
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
                                .height(PickersConstants.MEDIUM.dp)
                                .fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                showTimePicker.value = false
                            }) { Text("Cancel") }
                            TextButton(
                                onClick = {
                                    showTimePicker.value = false
                                    selectedTime.value = "${state.hour}:${state.minute}"
                                }
                            ) { Text("OK") }
                        }
                    }
                }
            }
        }
    }
}
