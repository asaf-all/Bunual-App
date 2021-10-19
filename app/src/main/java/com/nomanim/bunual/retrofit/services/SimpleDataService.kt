package com.nomanim.bunual.retrofit.services

import com.nomanim.bunual.retrofit.models.ModelSimpleData
import retrofit2.Call
import retrofit2.http.GET

interface SimpleDataService {

    @GET("iamasaf-profile/bunual-api-collection/master/simple_data.json")
    fun getData() : Call<ModelSimpleData>
}