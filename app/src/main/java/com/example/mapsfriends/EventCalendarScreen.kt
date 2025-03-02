package com.example.mapsfriends

import android.database.sqlite.SQLiteDatabase.OpenParams
import android.widget.AlphabetIndexer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.room.util.copy
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch
import com.google.maps.android.compose.Marker as Marker

@Preview
@Composable
fun EventCalendarScreen() {
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
        Column (
            modifier = Modifier
                .weight(1f)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {/* Выход на главный экран */ },
                    modifier = Modifier
                        .border(4.dp, Color.White, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.cross),
                        contentDescription = "Exit",
                        tint = Color.White
                    )
                }
                Text(
                    text = LocalContext.current.getString(R.string.my_events),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                dateData.forEach { date ->
                    Column {
                        TextButton(
                            onClick = { /* Переход на день */ },
                            modifier = Modifier
                                .width(44.dp)
                                .height(60.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = date.toString(),
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = weekDays.get(dateData.indexOf(date)).toString(),
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
            mockEvents.forEach { event ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.time,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Переход на ивент */ }
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = event.name,
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                            )
                            Row() {
                                event.members.forEach { member ->
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "MemberIcon",
                                        tint = colorResource(R.color.main_purple)
                                    )
                                }
                                Text(
                                    text = event.members.size.toString() + "/" + mockUsers.size.toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(horizontal = 4.dp)
                                )
                            }
                            Text(
                                text = event.day.toString() + " " + monthList.get(event.month - 1),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                            Text(
                                text = "~" + event.time,
                                fontSize = 12.sp,
                            )
                        }
                        IconButton(
                            onClick = {/* Удаление ивента */ },
                            modifier = Modifier
                                .border(
                                    2.dp,
                                    colorResource(R.color.main_pink),
                                    RoundedCornerShape(16.dp)
                                )
                                .align(Alignment.CenterVertically)

                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.cross),
                                contentDescription = "Delete",
                                tint = colorResource(R.color.main_pink),
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TextButton(
                onClick = {/* Переход в заявки на ивенты */ },
                modifier = Modifier
                    .height(64.dp)
                    .width(104.dp)
                    .border(4.dp, colorResource(R.color.main_blue), RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.requests),
                    fontSize = 20.sp,
                    color = colorResource(R.color.main_blue),
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(
                onClick = {/* Обновление данных ивентов */ },
                modifier = Modifier
                    .height(64.dp)
                    .width(104.dp)
                    .border(4.dp, colorResource(R.color.main_purple), RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.my_events),
                    fontSize = 20.sp,
                    color = colorResource(R.color.main_purple),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            TextButton(
                onClick = {/* Переход на создание нового ивента */},
                modifier = Modifier
                    .height(64.dp)
                    .width(104.dp)
                    .border(4.dp, colorResource(R.color.main_pink), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.new_event),
                    fontSize = 20.sp,
                    color = colorResource(R.color.main_pink),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}