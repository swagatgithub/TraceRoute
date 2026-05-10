package com.example.traceroute.presentation.map

import com.google.android.gms.maps.model.LatLng

data class MapUiState(

    val source: LatLng? = null,

    val destination: LatLng? = null,

    val routePoints: List<LatLng> = emptyList(),

    val movingMarkerPosition: LatLng? = null,

    val isLoading: Boolean = false
)