package com.example.maps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyViewModel : ViewModel() {

    private val _location = MutableStateFlow(LatLng(0.0, 0.0))
    val location: StateFlow<LatLng> = _location.asStateFlow()

    fun updateLocation(lat: Double, lon: Double) {
        _location.value = LatLng(lat, lon)
        Log.d("Location2", "Latitude: ${lat}, Longitude: ${lon}")
    }

}