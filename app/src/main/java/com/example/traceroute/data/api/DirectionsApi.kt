package com.example.traceroute.data.api

import com.example.traceroute.data.model.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApi {

    @GET("maps/api/directions/json")
    suspend fun getDirections(

        @Query("origin")
        origin: String,

        @Query("destination")
        destination: String,

        @Query("key")
        apiKey: String
    ): DirectionsResponse
}