package com.example.traceroute.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traceroute.data.repository.MapsRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.traceroute.utils.interpolate
import kotlinx.coroutines.delay

@HiltViewModel
class MapViewModel @Inject constructor(private val repository: MapsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    fun updateSource(source: LatLng) {
        _uiState.update {
            it.copy(source = source)
        }
        if (_uiState.value.destination != null) {
            fetchRoute()
        }
    }

    fun updateDestination(destination: LatLng) {
        _uiState.update {
            it.copy(destination = destination)
        }
        fetchRoute()
    }

    private fun fetchRoute() {

        val source = _uiState.value.source ?: return

        val destination = _uiState.value.destination ?: return

        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }

            val route = repository.getRoute(source, destination)

            _uiState.update {
                it.copy(
                    routePoints = route,
                    movingMarkerPosition = route.firstOrNull(),
                    isLoading = false
                )
            }

            animateMarker(route)
        }
    }

    private suspend fun animateMarker(route: List<LatLng>) {

        if (route.size < 2) return

        for (index in 0 until route.lastIndex) {

            val start =
                route[index]

            val end =
                route[index + 1]

            val steps = 60

            for (step in 0..steps) {

                val fraction =
                    step / steps.toFloat()

                val position = interpolate(fraction, start, end)

                updateMovingMarker(
                    position
                )

                delay(16L)
            }
        }
    }

    fun updateMovingMarker(latLng: LatLng) {
        _uiState.update {
            it.copy(
                movingMarkerPosition = latLng
            )
        }
    }
}