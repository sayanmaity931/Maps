package com.example.maps

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen() {
    val context = LocalContext.current

    val placesClient = Places.createClient(context)
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    val atasehir by remember {  mutableStateOf( LatLng(22.5744, 88.3629) )}
    val markerState by remember { mutableStateOf(MarkerState(position = atasehir)) }
    val focusManager = LocalContext.current
        .let { androidx.compose.ui.platform.LocalFocusManager.current }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(atasehir, 15f)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar with Autocomplete
        TextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                fetchPlacePredictions(query.text, placesClient) { newPredictions ->
                    predictions = newPredictions
                }
            },
            placeholder = { Text(text = "Enter a City Name") },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {

                }
            )
        )

        // Display Search Suggestions
        predictions.forEach { prediction ->
            TextButton(onClick = {
                searchQuery = TextFieldValue(
                    text = prediction.getPrimaryText(null).toString(),
                    selection = TextRange(prediction.getPrimaryText(null).toString().length)
                )
                moveCameraToLocation(prediction.placeId, placesClient, cameraPositionState , markerState )
                predictions = emptyList()

                focusManager.clearFocus()

                    }
                ) {
                Text(prediction.getPrimaryText(null).toString())
            }
        }

        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
         ){
            Marker(state = markerState, title = "Selected Location")
         }
    }
}


// Fetch Autocomplete Predictions
fun fetchPlacePredictions(query: String, placesClient: PlacesClient, onResult: (List<AutocompletePrediction>) -> Unit) {
    val token = AutocompleteSessionToken.newInstance()
    val request = FindAutocompletePredictionsRequest.builder()
        .setSessionToken(token)
        .setQuery(query)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            onResult(response.autocompletePredictions)
        }
        .addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Error fetching predictions: ${exception.message}")
        }
    }


// Move Camera to Selected Location
fun moveCameraToLocation(
    placeId: String,
    placesClient: PlacesClient,
    cameraPositionState: CameraPositionState,
    markerState: MarkerState
) {
    // Fetch place details (e.g., LatLng) and update camera position
    val placeFields = listOf(Place.Field.LAT_LNG) // Request only LatLng field
    val request = FetchPlaceRequest.builder(placeId, placeFields).build()

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response ->
            val place = response.place
            val latLng: LatLng? = place.latLng

            if (latLng != null) {
                // Move the camera to the selected location
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)

                // Update the marker position
                markerState.position = latLng
            } else {
                Log.e("moveCameraToLocation", "LatLng is null for place ID: $placeId")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("moveCameraToLocation", "Failed to fetch place details: ${exception.message}")
        }
}
