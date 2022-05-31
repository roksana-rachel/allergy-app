package edu.ib.allergyapp.pollens

import edu.ib.allergyapp.airQuality.AirData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PollenService {

    @GET("v2.0/current/airquality?")
    fun getPollenData (
        @Query("lat") lat : String,
        @Query("lon") lon : String,
        @Query("key") key : String
    ) : Call<PollenData>
}