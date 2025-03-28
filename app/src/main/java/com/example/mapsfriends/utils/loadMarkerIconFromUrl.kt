package com.example.mapsfriends.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.example.mapsfriends.R
import kotlin.math.pow


suspend fun loadOriginalBitmapFromUrl(context: Context, url: String): Bitmap? {
    return try {
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()
        val result = context.imageLoader.execute(request)
        result.drawable?.toBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun calculateMarkerSize(zoom: Float): Int {
    val baseZoom = 18f
    val scaleFactor = 2.5f
    val baseSize = 256

    return (baseSize * (zoom / baseZoom).pow(scaleFactor))
        .toInt()
        .coerceIn(16, 200)
}

fun createMarkerWithBorderAndTail(
    context: Context,
    original: Bitmap,
    size: Int
): Bitmap {
    val borderWidthPx = (size * 0.05f).toInt().coerceIn(2, 8)
    val tailHeightPx = (size * 0.2f).toInt().coerceIn(4, 16)
    val cornerRadiusPx = (size * 0.25f).toInt()

    val contentSize = size - 2 * borderWidthPx
    val totalHeight = size + tailHeightPx

    val result = Bitmap.createBitmap(size, totalHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)

    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.marker_border)
    }

    val mainRect = RectF(0f, 0f, size.toFloat(), size.toFloat())
    canvas.drawRoundRect(mainRect, cornerRadiusPx.toFloat(), cornerRadiusPx.toFloat(), bgPaint)

    val tailPath = Path().apply {
        moveTo(size * 0.4f, size.toFloat())
        lineTo(size * 0.6f, size.toFloat())
        lineTo(size * 0.5f, totalHeight.toFloat())
        close()
    }
    canvas.drawPath(tailPath, bgPaint)

    val scaledBitmap = Bitmap.createScaledBitmap(
        original,
        contentSize,
        contentSize,
        true
    )

    val mask = Bitmap.createBitmap(contentSize, contentSize, Bitmap.Config.ARGB_8888)
    Canvas(mask).apply {
        drawRoundRect(
            0f,
            0f,
            contentSize.toFloat(),
            contentSize.toFloat(),
            cornerRadiusPx.toFloat(),
            cornerRadiusPx.toFloat(),
            Paint(Paint.ANTI_ALIAS_FLAG)
        )
    }

    val combined = Bitmap.createBitmap(contentSize, contentSize, Bitmap.Config.ARGB_8888)
    Canvas(combined).apply {
        drawBitmap(mask, 0f, 0f, null)
        drawBitmap(scaledBitmap, 0f, 0f, Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        })
    }

    val left = (size - contentSize) / 2f
    val top = (size - contentSize) / 2f
    canvas.drawBitmap(combined, left, top, null)


    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 4f
    }
    canvas.drawRoundRect(
        left - 1,
        top - 1,
        left + contentSize + 1,
        top + contentSize + 1,
        cornerRadiusPx.toFloat(),
        cornerRadiusPx.toFloat(),
        borderPaint
    )

    return result
}
