package com.nomanim.bax.retrofit.builder

import com.nomanim.bax.retrofit.service.PhoneBrandsService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhoneBrandsApi {

    companion object {

        private val BASE_URL: String = "https://raw.githubusercontent.com/"

        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhoneBrandsService::class.java)
    }
}