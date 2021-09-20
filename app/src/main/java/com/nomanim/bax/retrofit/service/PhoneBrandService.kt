package com.nomanim.bax.retrofit.service

import com.nomanim.bax.retrofit.listModels.PhoneBrandsList
import retrofit2.Call
import retrofit2.http.GET

interface PhoneBrandService {

    @GET("ilyasozkurt/mobilephone-brands-and-models/master/brands.json")
    fun getData() : Call<PhoneBrandsList>
}