package com.nomanim.bax.retrofit.service

import com.nomanim.bax.retrofit.models.ModelPlaces
import retrofit2.Call
import retrofit2.http.GET

interface RegionService {

    @GET("AsafHacioglu/regions-api/main/az.json?token=ASQRT2BIIITLSCUE7JYJISLBFZE7Q")
    fun getData() : Call<List<ModelPlaces>>
}