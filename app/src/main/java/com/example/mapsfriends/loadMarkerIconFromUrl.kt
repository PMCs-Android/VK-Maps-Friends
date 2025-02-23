package com.example.mapsfriends

import android.content.Context
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

suspend fun loadMarkerIconFromUrl(context: Context, url: String): BitmapDescriptor? {
    val bitmap = loadBitmapFromUrl(context, url)
    return bitmap?.let { BitmapDescriptorFactory.fromBitmap(it) }
}