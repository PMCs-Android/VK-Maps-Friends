package com.example.mapsfriends

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest

suspend fun loadOriginalBitmapFromUrl(context: Context, url: String): Bitmap? {
    return try {
        val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
        val result = context.imageLoader.execute(request)
        result.drawable?.toBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun calculateMarkerSize(zoom: Float): Int {
    return when {
        zoom > 18 -> 200
        zoom > 15 -> 150
        zoom > 12 -> 100
        else -> 30
    }
}