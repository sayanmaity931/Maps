package com.example.maps.service


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.maps.CHANNEL_ID
import com.example.maps.MainActivity.LocationHolder
import com.example.maps.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class LocationService : Service() {

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 900_000) // 15 minutes
            .apply {
                setIntervalMillis(900_000) // Optional, sets the desired interval again
                setMinUpdateIntervalMillis(900_000) // Prevents receiving updates more frequently
            }.build()
    }


    internal val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {

            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }


            override fun onLocationResult(location: LocationResult) {
                val location = location.lastLocation!!
                val lat = location.latitude
                val lon = location.longitude
                LocationHolder.onLocationUpdate?.invoke(lat, lon)
                createNotificationChannel(lat , lon)
                Log.d("Location1", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object{
        var isRunning = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_LOCATION_SERVICE" -> {
                if (!isRunning) {
                    locationUpdates()
                    isRunning = true
                }
            }

            "STOP_LOCATION_SERVICE" -> {
                stopForeground(true)
                stopSelf()
                isRunning = false
            }
        }
        return START_STICKY
    }

    private fun locationUpdates(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else{
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel(lat : Double , lon : Double) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location : ")
            .setContentText("Latitude : $lat , Longitude : $lon")
            .build()

        startForeground(1, notification)
    }
}