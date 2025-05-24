package com.example.mapsfriends

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PedometerScreen(navController: NavHostController) {
    val data = remember {
        listOf(
            "26.05" to Dimensions.STEPS_DAY_1,
            "27.05" to Dimensions.STEPS_DAY_2,
            "28.05" to Dimensions.STEPS_DAY_3,
            "29.05" to Dimensions.STEPS_DAY_4,
            "30.05" to Dimensions.STEPS_DAY_5,
            "01.06" to Dimensions.NO_STEPS
        )
    }

    val targetLevels = listOf(Dimensions.LEVEL_1, Dimensions.LEVEL_2, Dimensions.LEVEL_3)

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
                .height(Dimensions.LARGE_ELEMENT_2.dp)
                .background(Color.White, RoundedCornerShape(Dimensions.MEDIUM_SPACING_1.dp))
        ) {
            Histogram(
                data = data,
                levels = targetLevels,
                maxHeight = Dimensions.BAR_HEIGHT.dp,
                barWidth = Dimensions.MEDIUM_SPACING_2.dp,
                spacing = Dimensions.NINE.dp
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
            fontSize = DateTimePickers.DEFAULT_PADDING.sp,
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
    maxHeight: Dp,
    barWidth: Dp,
    spacing: Dp
) {
    val maxValue = data.maxOfOrNull { it.second } ?: 1

    CanvasDraw(levels, maxValue)

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
                        fontSize = Dimensions.SMALL_PADDING_2.sp,
                        modifier = Modifier.padding(top = Dimensions.BORDER_WIDTH.dp)
                    )
                    Box(
                        modifier = Modifier.height(maxHeight * heightRatio).width(barWidth)
                            .background(
                                colorResource(R.color.main_purple),
                                RoundedCornerShape(
                                    topStart = Dimensions.BORDER_WIDTH.dp,
                                    topEnd = Dimensions.BORDER_WIDTH.dp
                                )
                            )
                    )
                }
                Text(
                    text = label,
                    fontSize = Dimensions.SMALL_PADDING_2.sp,
                    modifier = Modifier.width(barWidth + spacing),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CanvasDraw(levels: List<Int>, maxValue: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        levels.forEach { level ->
            val yPos = size.height * (1 - level.toFloat() / maxValue)
            drawLine(
                color = Color.Gray,
                start = Offset(0f, yPos),
                end = Offset(size.width, yPos),
                strokeWidth = 1.dp.toPx()
            )

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    level.toString(),
                    size.width - Dimensions.NINE.dp.toPx(),
                    yPos - Dimensions.NINE.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = Color.Gray.toArgb()
                        textSize = Dimensions.SMALL_PADDING_2.sp.toPx()
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }
        }
    }
}
