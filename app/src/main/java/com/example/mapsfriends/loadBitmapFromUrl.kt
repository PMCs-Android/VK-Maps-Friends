package com.example.mapsfriends

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest

suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? {
    val imageLoader = ImageLoader(context)
    return try {
        val request = ImageRequest.Builder(context)
            .data(url)
            .size(200,200) // Размер изображенияdsSDGDSgsdgs
            .allowHardware(false) // Отключаем hardware-битмапы для совместимости с Google Maps
            .build()

        val result = imageLoader.execute(request)
        result.drawable?.toBitmap() // Преобразуем Drawable в Bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}