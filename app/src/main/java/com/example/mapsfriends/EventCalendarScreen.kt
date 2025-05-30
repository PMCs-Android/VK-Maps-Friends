package com.example.mapsfriends

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.mapsfriends.login.AuthViewModel
import java.time.LocalDate

@Composable
fun EventCalendarScreen(
    navController: NavHostController
) {
    val authViewModel = hiltViewModel<AuthViewModel>()
    val creatorId = authViewModel.getCurrentUserId()!!
    LaunchedEffect(creatorId) {
        authViewModel.getUser(creatorId)
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
            .padding(horizontal = 10.dp, vertical = 30.dp)
    ) {
        MyEventsHeader(navController)
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            NotEmptyEvents(navController, creatorId)
        }
        BottomBar(navController)
    }
}

@Composable
fun MyEventsHeader(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { navController.navigate("main") }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cross),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = stringResource(R.string.my_events),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun NotEmptyEvents(
    navController: NavHostController,
    creatorId: String
) {
    val viewModel = hiltViewModel<EventViewModel>()
    val events = viewModel.events.collectAsState().value
    val selectedMonth = viewModel.selectedMonth.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadEventsForUser(creatorId)
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        item {
            AllMonthButton(viewModel, selectedMonth)
            Spacer(modifier = Modifier.width(8.dp))
            for (i in LocalDate.now().monthValue - 1..Dimensions.ELEVEN) {
                TextButton(
                    onClick = {
                        viewModel.filterEventsByMonth(i)
                    },
                    modifier = Modifier
                        .height(Dimensions.MEDIUM_SPACING_4.dp)
                        .background(
                            color = if (selectedMonth == i)
                                Color(
                                    Dimensions.COLOR,
                                    Dimensions.COLOR,
                                    Dimensions.COLOR,
                                    Dimensions.ALPHA
                                )
                            else Color(Dimensions.COLOR, Dimensions.COLOR, Dimensions.COLOR),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        text = fullMonthList[i],
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
    LazyColumn {
        items(events) { event ->
            if (event.time == "") {
                viewModel.deleteEvent(event.eventId, creatorId)
            } else {
                OneEvent(event, viewModel, navController, creatorId)
            }
        }
    }
}

@Composable
fun AllMonthButton(viewModel: EventViewModel, selectedMonth: Int?) {
    TextButton(
        onClick = { viewModel.resetMonthFilter() },
        modifier = Modifier.height(Dimensions.MEDIUM_SPACING_4.dp)
            .background(
                color = if (selectedMonth == null)
                    Color(Dimensions.COLOR, Dimensions.COLOR, Dimensions.COLOR, Dimensions.ALPHA)
                else Color(Dimensions.COLOR, Dimensions.COLOR, Dimensions.COLOR),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = "Все",
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}

@Composable
fun OneEvent(
    event: Event,
    viewModel: EventViewModel,
    navController: NavHostController,
    creatorId: String
) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val avatars = userViewModel.avatarsPerEvent.collectAsState().value[event.eventId] ?: emptyMap()

    LaunchedEffect(event.eventId) {
        userViewModel.observeEventAvatars(event.eventId, event.participants)
    }

    val day = event.time.slice(0..1).toInt()
    val month = event.time.slice(Dimensions.THREE..4).toInt()
    val clockTime = event.time.slice(Dimensions.SIX..10)
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("eventDetails/${event.eventId}") }
                .weight(1f).background(Color.White, RoundedCornerShape(20.dp)).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EventDateBox(day, month)
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
                Row {
                    avatars.forEach { participantAvatar ->
                        AsyncImage(
                            model = participantAvatar.value,
                            contentDescription = "Friend Avatar",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
                Text(
                    text = event.description,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = getWeekdayFromDate(day, month) + ", " + clockTime,
                    fontSize = 12.sp,
                )
            }
            DeleteButton(event, viewModel, creatorId)
        }
    }
}

@Composable
fun EventDateBox(day: Int, month: Int) {
    Column(
        modifier = Modifier
            .height(60.dp)
            .width(60.dp)
            .background(colorResource(R.color.light_purple), RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            fontWeight = FontWeight(Dimensions.LARGE_ELEMENT_4),
            fontSize = 24.sp,
            color = colorResource(R.color.main_purple)
        )
        Text(
            text = monthList[month - 1],
            fontWeight = FontWeight(Dimensions.LARGE_ELEMENT_6),
            fontSize = 16.sp,
            color = colorResource(R.color.main_purple)
        )
    }
}

@Composable
fun DeleteButton(event: Event, viewModel: EventViewModel, creatorId: String) {
    IconButton(
        onClick = { /* Удаление ивента */
            viewModel.deleteEvent(event.eventId, creatorId)
        },
        modifier = Modifier
            .border(
                2.dp,
                colorResource(R.color.main_pink),
                RoundedCornerShape(16.dp)
            )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.delete_cross),
            contentDescription = "Delete",
            tint = colorResource(R.color.main_pink)
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        TextButton(
            onClick = {
                navController.navigate("requests")
            },
            modifier = Modifier
                .height(64.dp)
                .width(104.dp)
                .border(4.dp, colorResource(R.color.main_blue), RoundedCornerShape(12.dp))
        ) {
            Text(
                text = stringResource(R.string.requests),
                fontSize = 20.sp,
                color = colorResource(R.color.main_blue),
                fontWeight = FontWeight.Bold
            )
        }
        TextButton(
            onClick = { /* Обновление данных ивентов */ },
            modifier = Modifier
                .height(64.dp)
                .width(104.dp)
                .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(12.dp))
        ) {
            Text(
                text = stringResource(R.string.my_events),
                fontSize = 20.sp,
                color = colorResource(R.color.main_purple),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        TextButton(
            onClick = { navController.navigate("create") },
            modifier = Modifier
                .height(64.dp)
                .width(104.dp)
                .border(4.dp, colorResource(R.color.main_pink), RoundedCornerShape(16.dp))
        ) {
            Text(
                text = stringResource(R.string.new_event),
                fontSize = 20.sp,
                color = colorResource(R.color.main_pink),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
