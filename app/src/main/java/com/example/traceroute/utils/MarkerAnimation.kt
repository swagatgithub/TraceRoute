package com.example.traceroute.utils

import com.google.android.gms.maps.model.LatLng

fun interpolate(fraction: Float, start: LatLng, end: LatLng): LatLng {
    val latitude = (end.latitude - start.latitude)* fraction + start.latitude
    val longitude = (end.longitude - start.longitude)* fraction + start.longitude
    return LatLng(latitude, longitude)
}