package com.nomanim.bax.retrofit.service

import com.nomanim.bax.retrofit.listModels.PhoneModelsList
import retrofit2.Call
import retrofit2.http.GET

interface PhoneModelService {

    @GET("ilyasozkurt/mobilephone-brands-and-models/master/devices.json")
    fun getData() : Call<PhoneModelsList>
}