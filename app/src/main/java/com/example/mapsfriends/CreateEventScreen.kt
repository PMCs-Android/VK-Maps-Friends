package com.example.mapsfriends

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.GeoPoint

@Composable
//@Preview
fun CreateEventScreen(navController: NavHostController) {
    val title = remember { mutableStateOf("") }
    val descrip = remember { mutableStateOf("") }
    val datetime = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    val time = remember { mutableStateOf("") }
    val location = remember { mutableStateOf(GeoPoint(0.0, 0.0)) }

    val viewModel = hiltViewModel< EventViewModel>()
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

        // title input
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        // date time input
        Row() {
            TextField(
                value = date.value,
                onValueChange = { date.value = it },
                textStyle = TextStyle(fontSize = 20.sp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
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
                        text = "Дата",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TextField(
                value = time.value,
                onValueChange = { time.value = it },
                textStyle = TextStyle(fontSize = 20.sp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
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
                        text = "Время",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
        // description input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .height(80.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
        ) {
            TextField(
                value = descrip.value,
                onValueChange = { descrip.value = it },
                textStyle = TextStyle(fontSize = 20.sp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxSize(),
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInput(openDialog: MutableState<Boolean>, state: DatePickerState) {

    //            val state = rememberDatePickerState()
//            val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
            ),
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = colorResource(R.color.main_blue),
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray,
                    ),
                    border = BorderStroke(2.dp, colorResource(R.color.main_blue)),
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = colorResource(R.color.main_pink),
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray,
                    ),
                    border = BorderStroke(2.dp, colorResource(R.color.main_pink)),
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("CANCEL")
                }
            }
        ) {
            DatePicker(
                state = state
            )
        }
    }
}