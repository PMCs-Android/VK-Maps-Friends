package com.example.mapsfriends

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class LocationManager(private val context: Context) {

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            locationResult.lastLocation?.let { location ->
                listener?.onLocationUpdated(location)
            }
        }
    }

    var listener: OnLocationUpdateListener? = null

    interface OnLocationUpdateListener {
        fun onLocationUpdated(location: Location)
        fun onPermissionDenied()
    }

    fun startLocationUpdates() {
        if (hasLocationPermissions()) {
            requestLocationUpdates()
        } else {
            listener?.onPermissionDenied()
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
                hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun requestLocationUpdates() {
        try {
            if (!hasLocationPermissions()) {
                listener?.onPermissionDenied()
                return
            }

            val locationRequest = LocationRequest.Builder(10000)
                .setMinUpdateIntervalMillis(5000)
                .setPriority(Priority. PRIORITY_HIGH_ACCURACY)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            listener?.onPermissionDenied()
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
