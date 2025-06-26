package com.example.maps

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.maps.service.LocationService
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "channel_name"

class BaseApplication : Application() {

    lateinit var placesClient: PlacesClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        Places.initialize(applicationContext, "AIzaSyDcTX1zSasMtrVmL8p1UcOctqPRkK94lVM")
        placesClient = Places.createClient(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
//        startForegroundService(Intent(this, LocationService::class.java))
    }
}