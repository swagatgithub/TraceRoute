package com.example.traceroute.data.repository

import com.example.traceroute.BuildConfig
import com.example.traceroute.data.api.DirectionsApi
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import javax.inject.Inject

class MapsRepository @Inject constructor(private val directionsApi: DirectionsApi) {
    suspend fun getRoute(source: LatLng, destination: LatLng): List<LatLng> {

        val response =
            directionsApi.getDirections(
                origin =
                    "${source.latitude},${source.longitude}",
                destination =
                    "${destination.latitude},${destination.longitude}",
                apiKey = BuildConfig.MAPS_API_KEY
            )

        val encodedPolyline =
            response.routes.firstOrNull()
                ?.overview_polyline
                ?.points
                ?: ""

        return PolyUtil.decode(encodedPolyline)
    }
}