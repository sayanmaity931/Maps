package com.example.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.maps.service.LocationService
import com.example.maps.ui.theme.MapsTheme
import com.example.maps.viewmodel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {

    object LocationHolder {
        var onLocationUpdate: ((Double, Double) -> Unit)? = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("PermissionLaunchedDuringComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MapsTheme {

                val permission = rememberMultiplePermissionsState(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                        listOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        listOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    }else{
                        listOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    }
                )
                LaunchedEffect(Unit) {
                    permission.launchMultiplePermissionRequest()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location"
                            )
                        }
                    }
                ) { innerPadding ->
                    if (permission.allPermissionsGranted) {
                            Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                                MapScreen()
                            }
                    }else if(permission.shouldShowRationale){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(onClick = {
                                permission.launchMultiplePermissionRequest()
                            }) {
                                Text(text = "Give Permission")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startLocationServiceIfNotRunning(this)
    }

    override fun onStop() {
        super.onStop()
        stopLocationServiceIfRunning(this)  // ← Only if you want it to stop when UI closes
    }

    fun startLocationServiceIfNotRunning(context: Context) {
        if (!LocationService.isRunning) {
            val intent = Intent(context, LocationService::class.java).apply {
                action = "START_LOCATION_SERVICE"
            }
            context.startService(intent)
        }
    }

    fun stopLocationServiceIfRunning(context: Context) {
        if (LocationService.isRunning) {
            val intent = Intent(context, LocationService::class.java).apply {
                action = "STOP_LOCATION_SERVICE"
            }
            context.stopService(intent)
        }
    }
}



