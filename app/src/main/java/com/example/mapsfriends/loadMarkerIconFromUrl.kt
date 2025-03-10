package com.example.mapsfriends

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
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