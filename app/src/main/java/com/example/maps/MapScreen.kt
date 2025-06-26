package com.example.maps

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maps.MainActivity.LocationHolder
import com.example.maps.viewmodel.MyViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {

    val viewModel: MyViewModel = viewModel()

    val location = viewModel.location.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        LocationHolder.onLocationUpdate = { lat, lon ->
            viewModel.updateLocation(lat, lon)
        }
    }

    val atasehir by rememberSaveable { mutableStateOf( LatLng(location.value.latitude , location.value.longitude) ) }
    Log.d("Location", "Latitude: ${location.value.latitude}, Longitude: ${location.value.longitude}")
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(atasehir, 15f)
    }

    var markerState by rememberSaveable { mutableStateOf<LatLng?>(null) }
    var isCameraManuallyMoved by remember { mutableStateOf(false) }

    LaunchedEffect(location.value) {
        if (!isCameraManuallyMoved && location.value.latitude != 0.0 && location.value.longitude != 0.0) {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.value.latitude, location.value.longitude),
                    15f
                )
            )
            markerState = LatLng(location.value.latitude, location.value.longitude)
            isCameraManuallyMoved = true
        }
    }

    val app = LocalContext.current.applicationContext as BaseApplication
    val placesClient = app.placesClient

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var searchQuery1 by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    val focusManager = LocalContext.current
        .let { LocalFocusManager.current }
    var uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    val routeCoordinates = listOf(
        LatLng(22.57286, 88.364017),
        LatLng(22.572937, 88.363849),
        LatLng(22.572991, 88.36374),
        LatLng(22.573238, 88.363874),
        LatLng(22.573373, 88.363938),
        LatLng(22.573345, 88.364033),
        LatLng(22.573233, 88.364295),
        LatLng(22.573712, 88.364527),
        LatLng(22.573569, 88.364811),
        LatLng(22.573566, 88.364821),
        LatLng(22.573358, 88.365421),
        LatLng(22.57327, 88.365685),
        LatLng(22.573077, 88.36614),
        LatLng(22.572973, 88.366509),
        LatLng(22.572788, 88.366997),
        LatLng(22.57277, 88.36705),
        LatLng(22.572563, 88.367597),
        LatLng(22.572485, 88.367769),
        LatLng(22.572443, 88.367924),
        LatLng(22.571787, 88.368372),
        LatLng(22.571647, 88.368467),
        LatLng(22.571452, 88.368618),
        LatLng(22.571012, 88.368868),
        LatLng(22.570612, 88.369085),
        LatLng(22.570038, 88.369399),
        LatLng(22.568757, 88.370149),
        LatLng(22.570057, 88.370799),
        LatLng(22.570184, 88.370863),
        LatLng(22.570801, 88.371125),
        LatLng(22.570872, 88.371156),
        LatLng(22.570996, 88.37121),
        LatLng(22.571155, 88.371279),
        LatLng(22.571182, 88.371291),
        LatLng(22.571974, 88.371679),
        LatLng(22.572678, 88.37199),
        LatLng(22.573132, 88.372216),
        LatLng(22.57338, 88.372339),
        LatLng(22.573424, 88.372363),
        LatLng(22.574075, 88.372645),
        LatLng(22.574375, 88.372798),
        LatLng(22.574948, 88.373058),
        LatLng(22.575653, 88.37326),
        LatLng(22.57569, 88.373265),
        LatLng(22.575856, 88.37329),
        LatLng(22.576666, 88.373411),
        LatLng(22.578037, 88.3736),
        LatLng(22.578206, 88.373626),
        LatLng(22.578488, 88.37367),
        LatLng(22.579102, 88.373742),
        LatLng(22.579179, 88.373751),
        LatLng(22.580114, 88.373911),
        LatLng(22.580497, 88.373962),
        LatLng(22.580692, 88.374002),
        LatLng(22.581182, 88.374074),
        LatLng(22.581727, 88.374191),
        LatLng(22.582589, 88.374327),
        LatLng(22.582677, 88.374339),
        LatLng(22.583322, 88.374436),
        LatLng(22.583526, 88.374466),
        LatLng(22.583822, 88.374497),
        LatLng(22.583908, 88.374508),
        LatLng(22.584051, 88.374521),
        LatLng(22.584906, 88.374634),
        LatLng(22.585099, 88.37466),
        LatLng(22.585161, 88.374668),
        LatLng(22.585268, 88.374683),
        LatLng(22.58536, 88.374686),
        LatLng(22.585339, 88.374789),
        LatLng(22.585311, 88.374876),
        LatLng(22.58529, 88.374942),
        LatLng(22.585213, 88.375182),
        LatLng(22.585056, 88.375668),
        LatLng(22.584995, 88.375857),
        LatLng(22.584834, 88.376315),
        LatLng(22.584711, 88.376634),
        LatLng(22.584649, 88.376761),
        LatLng(22.584576, 88.376963),
        LatLng(22.584491, 88.377195),
        LatLng(22.584418, 88.377398),
        LatLng(22.584391, 88.377459),
        LatLng(22.584201, 88.377913),
        LatLng(22.584182, 88.377964),
        LatLng(22.584115, 88.378153),
        LatLng(22.584019, 88.378404),
        LatLng(22.583799, 88.378976),
        LatLng(22.58352, 88.379618),
        LatLng(22.583224, 88.380361),
        LatLng(22.582914, 88.381079),
        LatLng(22.58285, 88.381221),
        LatLng(22.582824, 88.381293),
        LatLng(22.582783, 88.381394),
        LatLng(22.582724, 88.381543),
        LatLng(22.582318, 88.382592),
        LatLng(22.582221, 88.38283),
        LatLng(22.582013, 88.38336),
        LatLng(22.581965, 88.383477),
        LatLng(22.58192, 88.383588),
        LatLng(22.58165, 88.384294),
        LatLng(22.581539, 88.384594),
        LatLng(22.581463, 88.384778),
        LatLng(22.581395, 88.384953),
        LatLng(22.58132, 88.385153),
        LatLng(22.581214, 88.385475),
        LatLng(22.5812, 88.385516),
        LatLng(22.581118, 88.385752),
        LatLng(22.581036, 88.386087),
        LatLng(22.580982, 88.386299),
        LatLng(22.580898, 88.38655),
        LatLng(22.580767, 88.386846),
        LatLng(22.580697, 88.386991),
        LatLng(22.580589, 88.387227),
        LatLng(22.580534, 88.387347),
        LatLng(22.580484, 88.387474),
        LatLng(22.580433, 88.387627),
        LatLng(22.580407, 88.387754),
        LatLng(22.580392, 88.387982),
        LatLng(22.580382, 88.3884),
        LatLng(22.580347, 88.388774),
        LatLng(22.580321, 88.389037),
        LatLng(22.580309, 88.389173),
        LatLng(22.580274, 88.389784),
        LatLng(22.58025, 88.390275),
        LatLng(22.580257, 88.390412),
        LatLng(22.580281, 88.390433),
        LatLng(22.5803, 88.390458),
        LatLng(22.580341, 88.390488),
        LatLng(22.580378, 88.390504),
        LatLng(22.580399, 88.390539),
        LatLng(22.580649, 88.390619),
        LatLng(22.581616, 88.390878),
        LatLng(22.58195, 88.39097),
        LatLng(22.582075, 88.391006),
        LatLng(22.583031, 88.391272),
        LatLng(22.583931, 88.391495),
        LatLng(22.584269, 88.391582),
        LatLng(22.584554, 88.391659),
        LatLng(22.584986, 88.391775),
        LatLng(22.586312, 88.392122),
        LatLng(22.5863, 88.392183),
        LatLng(22.586294, 88.392212),
        LatLng(22.586282, 88.392258),
        LatLng(22.585861, 88.39407),
        LatLng(22.585846, 88.394132),
        LatLng(22.585704, 88.394718),
        LatLng(22.585705, 88.394743),
        LatLng(22.585719, 88.394772),
        LatLng(22.585886, 88.395002),
        LatLng(22.5859, 88.395039),
        LatLng(22.585902, 88.395071),
        LatLng(22.58573, 88.395905),
        LatLng(22.585427, 88.397922),
        LatLng(22.585425, 88.398027),
        LatLng(22.585439, 88.39809),
        LatLng(22.585466, 88.398197),
        LatLng(22.585552, 88.398425),
        LatLng(22.585587, 88.398513),
        LatLng(22.585653, 88.398705),
        LatLng(22.585698, 88.398829),
        LatLng(22.585584, 88.398888),
        LatLng(22.585167, 88.399091),
        LatLng(22.584778, 88.399266),
        LatLng(22.584436, 88.399427),
        LatLng(22.584294, 88.39949),
        LatLng(22.584259, 88.399507),
        LatLng(22.584131, 88.399564),
        LatLng(22.583767, 88.399723),
        LatLng(22.583553, 88.399815),
        LatLng(22.583372, 88.399894),
        LatLng(22.583067, 88.400038),
        LatLng(22.582869, 88.400132),
        LatLng(22.582622, 88.400249),
        LatLng(22.582596, 88.400261),
        LatLng(22.582491, 88.400313),
        LatLng(22.582027, 88.400545),
        LatLng(22.58171, 88.400703),
        LatLng(22.581345, 88.400887),
        LatLng(22.581376, 88.400923),
        LatLng(22.581414, 88.400953),
        LatLng(22.581469, 88.400968),
        LatLng(22.581863, 88.401027),
        LatLng(22.582261, 88.401072),
        LatLng(22.582505, 88.401123),
        LatLng(22.582619, 88.401145),
        LatLng(22.582788, 88.401127),
        LatLng(22.583044, 88.401244),
        LatLng(22.583284, 88.401394),
        LatLng(22.583396, 88.401491),
        LatLng(22.583787, 88.402164),
        LatLng(22.584476, 88.403331),
        LatLng(22.584646, 88.403613),
        LatLng(22.585092, 88.403309),
        LatLng(22.585495, 88.403035),
        LatLng(22.585902, 88.402758),
        LatLng(22.585338, 88.401774)
    )

    val markerStateSearch = rememberMarkerState(position = atasehir)
//    val markerStateSearch = remember { mutableStateOf<LatLng?>(null) }
    val markerState1 by remember { mutableStateOf(MarkerState(position = routeCoordinates[0])) }
    val markerState2 by remember { mutableStateOf(MarkerState(position = routeCoordinates[routeCoordinates.size - 1])) }


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
            keyboardActions = KeyboardActions(onSearch = {})
        )

        // Display Search Suggestions
        predictions.forEach { prediction ->
            TextButton(onClick = {
                searchQuery = TextFieldValue(
                    text = prediction.getPrimaryText(null).toString(),
                    selection = TextRange(prediction.getPrimaryText(null).toString().length)
                )
                fetchPlaceDetails(prediction.placeId, placesClient , markerStateSearch)
                if ((location.value.latitude != 0.0 && location.value.longitude != 0.0) && (markerStateSearch.position.latitude == 0.0 && markerStateSearch.position.longitude == 0.0)) {
                    moveCameraToLocation(
                        prediction.placeId, placesClient, cameraPositionState,
                        markerState = MarkerState(position = LatLng(markerState?.latitude ?: 0.00,
                            markerState?.longitude ?: 0.00
                            )
                        )
                    )
                }else{
                    moveCameraToLocation(prediction.placeId, placesClient, cameraPositionState , markerState = markerStateSearch)
                }

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
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings,
            ){
                if (markerState != null) Marker(state = rememberMarkerState(position = markerState!!), title = "Ataşehir" , icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                if (markerStateSearch.position.latitude != 0.00 && markerStateSearch.position.longitude != 0.00) {
                        Marker(
                            state = markerStateSearch,
                            title = "Ataşehir",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                    }
                Marker(state = markerState1, title = "Starting" , icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                Marker(state = markerState2, title = "Destination" , icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                Polyline(
                    points = routeCoordinates,
                    clickable = true,
                    color = Color.Blue,
                    width = 20f
                )
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

fun fetchPlaceDetails(placeId: String, placesClient: PlacesClient , markerState: MarkerState) {

    // Fetch place details (e.g., LatLng) and update camera position
    val placeFields = listOf(Place.Field.LAT_LNG) // Request only LatLng field
    val request = FetchPlaceRequest.builder(placeId, placeFields).build()

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response ->
            val place = response.place
            val latLng: LatLng? = place.latLng
            if (latLng != null) {
                markerState.position = latLng
            }
        }.addOnFailureListener {
            Log.e("fetchPlaceDetails", "LatLng is null for place ID: $placeId")
        }
}
