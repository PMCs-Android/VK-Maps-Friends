package com.example.mapsfriends

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PedometerScreen(navController: NavHostController) {
    val data = remember {
        listOf(
            "26.05" to 3965,
            "27.05" to 7561,
            "28.05" to 4965,
            "29.05" to 5865,
            "30.05" to 7965,
            "01.06" to 0
        )
    }

    val targetLevels = listOf(3000, 6000, 10000) // Три целевых уровня

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
        PedometerHeader(navController)
//        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
        ) {
            Histogram(
                data = data,
                levels = targetLevels,
                barColor = colorResource(R.color.main_purple),
                levelColor = Color.Gray,
                maxHeight = 250.dp,
                barWidth = 30.dp,
                spacing = 8.dp
            )
        }
    }
}

@Composable
fun PedometerHeader(navController: NavHostController) {
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
            text = "Твои шаги",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun Histogram(
    data: List<Pair<String, Int>>,
    levels: List<Int>,
    barColor: Color,
    levelColor: Color,
    maxHeight: Dp,
    barWidth: Dp,
    spacing: Dp
) {
    val maxValue = data.maxOfOrNull { it.second } ?: 1

    Canvas(modifier = Modifier.fillMaxSize()) {
        levels.forEach { level ->
            val yPos = size.height * (1 - level.toFloat() / maxValue)
            drawLine(
                color = levelColor,
                start = Offset(0f, yPos),
                end = Offset(size.width, yPos),
                strokeWidth = 1.dp.toPx()
            )

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    level.toString(),
                    size.width - 8.dp.toPx(),
                    yPos - 8.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = levelColor.toArgb()
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        data.forEach { (label, value) ->
            val heightRatio = value.toFloat() / maxValue.toFloat()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = spacing / 2)
            ) {
                if (heightRatio != 0f) {
                    Text(
                        text = value.toString(),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .height(maxHeight * heightRatio)
                            .width(barWidth)
                            .background(
                                barColor,
                                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                }
                Text(
                    text = label,
                    fontSize = 12.sp,
                    modifier = Modifier.width(barWidth + spacing),
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}
// Подписи под столбцами
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = spacing),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                data.forEach { (label, _) ->
//                    Text(
//                        text = label,
//                        fontSize = 12.sp,
//                        modifier = Modifier.width(barWidth),
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }

//    Box(modifier = modifier) {
//        // Фон с линиями сетки
//        Canvas(modifier = Modifier.matchParentSize()) {
//            // Горизонтальные линии сетки
//            for (i in 1..4) {
//                val y = size.height * (1 - i * 0.2f)
//                drawLine(
//                    color = Color.LightGray.copy(alpha = 0.3f),
//                    start = Offset(0f, y),
//                    end = Offset(size.width, y),
//                    strokeWidth = 1.dp.toPx()
//                )
//            }
//        }
//
//        Column(modifier = modifier) {
//            Row(
//                verticalAlignment = Alignment.Bottom,
//                horizontalArrangement = Arrangement.Center,
//                modifier = Modifier.weight(1f)
//            ) {
//                data.forEach { (label, value) ->
//                    val heightRatio = value.toFloat() / maxValue.toFloat()
//
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.padding(horizontal = spacing / 2)
//                    ) {
//                        Text(
//                            text = value.toString(),
//                            fontSize = 12.sp,
//                            modifier = Modifier.padding(top = 4.dp)
//                        )
//                        Box(
//                            modifier = Modifier
//                                .height(maxBarHeight * heightRatio)
//                                .width(barWidth)
//                                .background(
//                                    barColor,
//                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
//                                )
//                        )
//                        Text(
//                            text = label,
//                            fontSize = 12.sp,
//                            modifier = Modifier.width(barWidth + spacing),
//                            textAlign = TextAlign.Center
//                        )
//                    }
//                }
//            }
//        }
//    }
