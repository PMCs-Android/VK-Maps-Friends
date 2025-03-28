import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ZoomSlider(
    modifier: Modifier = Modifier,
    onZoomChange: (Float) -> Unit,
    initialZoom: Float = 18f,
    sensitivity: Float = 0.002f
) {
    var startY by remember { mutableFloatStateOf(0f) }
    var currentZoom by remember { mutableFloatStateOf(initialZoom) }
    var touchOffset by remember { mutableStateOf(Offset.Zero) }
    var isVisible by remember { mutableStateOf(false) }
    var interaction by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "alphaAnimation"
    )

    val scope = rememberCoroutineScope()

    fun updateZoom(deltaY: Float) {
        val scaleFactor = 1f + (-deltaY * sensitivity)
        val newZoom = currentZoom * scaleFactor

        currentZoom = newZoom
        onZoomChange(newZoom)
    }

    Box(modifier = modifier.fillMaxHeight().width(64.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    if (alpha > 0.01f) {
                        drawRoundRect(
                            color = Color.Blue.copy(alpha = 0.2f * alpha),
                            cornerRadius = CornerRadius(4f, 4f),
                            topLeft = Offset(size.width - 8f, 0f),
                            size = Size(4f, size.height)
                        )

                        drawCircle(
                            color = Color.Blue.copy(alpha = 0.4f * alpha),
                            radius = 24f,
                            center = Offset(size.width / 2, touchOffset.y)
                        )
                    }
                }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
                            interaction = true
                            isVisible = true
                            startY = offset.y
                            touchOffset = offset
                        },
                        onVerticalDrag = { change, _ ->
                            touchOffset = change.position
                            val deltaY = (change.position.y - startY)
                            updateZoom(deltaY)
                            startY = change.position.y
                        },
                        onDragEnd = {
                            interaction = false
                            scope.launch {
                                delay(500)
                                if (!interaction) isVisible = false
                            }
                        }
                    )
                }
        )
    }
}