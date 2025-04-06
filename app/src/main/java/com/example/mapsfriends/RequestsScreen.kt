package com.example.mapsfriends

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun RequestsScreen(navController: NavHostController) {
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
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.border(4.dp, Color.White, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = LocalContext.current.getString(R.string.requests),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
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
                        .clickable { navController.navigate("requestDetails") }
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
                        Row {
                            event.members.forEach { member ->
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "MemberIcon"
                                )
                            }
                            Text(
                                text = event.members.size.toString() + "/"
                                        + mockUsers.size.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(horizontal = 4.dp)
                            )
                        }
                        Text(
                            text = event.day.toString() + " " + monthList[event.month - 1],
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            text = "~" + event.time,
                            fontSize = 12.sp,
                        )
                    }
                    Column(
                        modifier = Modifier
                    ) {
                        IconButton(
                            onClick = { /* Удаление ивента */ },
                            modifier = Modifier.border(
                                2.dp,
                                colorResource(R.color.main_blue),
                                RoundedCornerShape(16.dp)
                            )
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.acception),
                                contentDescription = "Delete",
                                tint = colorResource(R.color.main_blue),
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        IconButton(
                            onClick = { /* Удаление ивента */ }, modifier = Modifier.border(
                                2.dp,
                                colorResource(R.color.main_pink),
                                RoundedCornerShape(16.dp)
                            )
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
    }
}
