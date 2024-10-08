package com.example.mobiletest.Datas.API

import com.example.mobiletest.Datas.Model.APIResModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/v1/geocode")
    fun getLocations(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("lang") lang:String
    ): Call<APIResModel>
}
