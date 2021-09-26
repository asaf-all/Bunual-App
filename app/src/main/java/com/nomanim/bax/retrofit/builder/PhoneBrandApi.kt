package com.nomanim.bax.retrofit.builder

import com.nomanim.bax.retrofit.service.PhoneBrandService
import com.nomanim.bax.retrofit.service.PhoneModelService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhoneBrandApi {

    companion object {

        private val BASE_URL: String = "https://raw.githubusercontent.com/"

        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhoneBrandService::class.java)
    }
}