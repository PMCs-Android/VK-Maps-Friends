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
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class LocationManager(private val context: Context) {

    interface OnLocationUpdateListener {
        fun onLocationUpdated(location: Location)
        fun onPermissionDenied()
        fun onLocationError(error: String)
    }

    var listener: OnLocationUpdateListener? = null
    private var userId: String? = null
    private var lastLocation: Location? = null
    private var lastUpdateTime: Long = 0
    private var isHighFrequencyMode = true

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                handleLocationUpdate(location)
            }
        }
    }

    private companion object {
        const val HIGH_FREQUENCY_INTERVAL_MILLIS = 5000L
        const val LOW_FREQUENCY_INTERVAL_MILLIS = 180000L
        const val LOCATION_CHANGE_THRESHOLD_METERS = 20f
        const val PRIORITY = Priority.PRIORITY_HIGH_ACCURACY
    }

    private fun hasLocationPermissions(): Boolean =
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

    fun startLocationUpdates() {
        if (!hasLocationPermissions()) {
            listener?.onPermissionDenied()
            return
        }

        try {
            requestLocationUpdates(HIGH_FREQUENCY_INTERVAL_MILLIS)
        } catch (e: SecurityException) {
            e.printStackTrace()
            listener?.onLocationError("SecurityException: ${e.message}")
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun requestLocationUpdates(intervalMillis: Long) {
        if (!hasLocationPermissions()) {
            listener?.onPermissionDenied()
            return
        }

        try {
            val locationRequest = LocationRequest.Builder(intervalMillis)
                .setMinUpdateIntervalMillis(intervalMillis / 2)
                .setPriority(PRIORITY)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            listener?.onLocationError("SecurityException: ${e.message}")
        }
    }

    private fun handleLocationUpdate(location: Location) {
        val currentTime = System.currentTimeMillis()
        val isSignificantChange = lastLocation == null ||
            location.distanceTo(lastLocation!!) >= LOCATION_CHANGE_THRESHOLD_METERS

        if (isSignificantChange) {
            if (!isHighFrequencyMode) {
                isHighFrequencyMode = true
                requestLocationUpdates(HIGH_FREQUENCY_INTERVAL_MILLIS)
            }
            lastLocation = location
            lastUpdateTime = currentTime
            listener?.onLocationUpdated(location)
            userId?.let { id ->
                sendLocationToFirestore(id, location.latitude, location.longitude)
            }
        } else {
            if (currentTime - lastUpdateTime > LOW_FREQUENCY_INTERVAL_MILLIS &&
                isHighFrequencyMode
            ) {
                isHighFrequencyMode = false
                requestLocationUpdates(LOW_FREQUENCY_INTERVAL_MILLIS)
            }
        }
    }

    private fun sendLocationToFirestore(
        userId: String,
        latitude: Double,
        longitude: Double
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val locationData = GeoPoint(latitude, longitude)
        firestore.collection("users")
            .document(userId)
            .update("location", locationData)
    }
}
