package com.nomanim.bax.retrofit.services

import com.nomanim.bax.retrofit.listModels.PhoneBrandsList
import retrofit2.Call
import retrofit2.http.GET

interface PhoneBrandsService {

    @GET("AsafHacioglu/bax-api-collection/master/brands.json")
    fun getData() : Call<PhoneBrandsList>
}