package com.example.traceroute.presentation.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.traceroute.presentation.components.SearchBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel(), innerPadding: PaddingValues) {

    val context = LocalContext.current

    val state by viewModel.uiState.collectAsState()

    val cameraPositionState = rememberCameraPositionState()

    var predictions by remember {
        mutableStateOf(
            emptyList<AutocompletePrediction>()
        )
    }

    var activeSearchType by rememberSaveable {
        mutableStateOf("")
    }

    var sourceQuery by rememberSaveable {
        mutableStateOf("")
    }

    var destinationQuery by rememberSaveable {
        mutableStateOf("")
    }

    val placesClient = remember {
        Places.createClient(context)
    }

    val source = state.source

    val destination = state.destination

    LaunchedEffect(source, destination) {

        if (source != null && destination != null) {

            val bounds =
                LatLngBounds.builder()
                    .include(source)
                    .include(destination)
                    .build()

            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 100))

        }
    }

    Column(modifier = Modifier.fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp)) {

        SearchBar(
            hint = "Search Source",
            value = sourceQuery,
            onValueChange = {
                sourceQuery = it
            },
            onPredictionsChanged = {
                predictions = it
                activeSearchType = "source"
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SearchBar(
            hint = "Search Destination",
            value = destinationQuery,
            onValueChange = {
                destinationQuery = it
            },
            onPredictionsChanged = {
                predictions = it
                activeSearchType = "destination"
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if(predictions.isNotEmpty()) {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {

                items(predictions) { prediction ->

                    Text(text = prediction.getFullText(null).toString(),
                        modifier = Modifier.fillMaxWidth().clickable {

                            val placeFields = listOf(Place.Field.LAT_LNG)

                            val request = FetchPlaceRequest.builder(prediction.placeId, placeFields).build()

                            placesClient
                                .fetchPlace(request)
                                .addOnSuccessListener {
                                    it.place.latLng
                                        ?.let { latLng ->
                                            if (activeSearchType == "source") {
                                                sourceQuery =
                                                    prediction
                                                        .getFullText(null)
                                                        .toString()
                                                viewModel.updateSource(latLng)
                                            } else {
                                                destinationQuery =
                                                    prediction
                                                        .getFullText(null)
                                                        .toString()
                                                viewModel.updateDestination(latLng)
                                            }
                                            predictions = emptyList()
                                        }
                                }
                        }.padding(16.dp)
                    )
                }
            }
        }

        GoogleMap(modifier = Modifier.weight(1f), cameraPositionState = cameraPositionState) {

            if (state.routePoints.isEmpty()) {
                state.source?.let { source ->
                    Marker(state = rememberUpdatedMarkerState(position = source), title = "Source")
                }
            }

            state.destination?.let { destination ->
                Marker(state = rememberUpdatedMarkerState(position = destination), title = "Destination")
            }

            if (state.routePoints.isNotEmpty()) {
                Polyline(points = state.routePoints, width = 12f)
            }

            state.movingMarkerPosition?.let { movingPosition ->
                Marker(state = rememberUpdatedMarkerState(position = movingPosition), title = "Moving Marker")
            }
        }
    }
}