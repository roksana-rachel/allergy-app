package edu.ib.allergyapp.airQuality

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirService {

    @GET("data/2.5/air_pollution?")
    fun getAirData (
        @Query("lat") lat : String,
        @Query("lon") lon : String,
        @Query("AppId") AppId : String
    ) : Call<AirData>
}