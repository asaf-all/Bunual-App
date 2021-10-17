package com.nomanim.bunual.retrofit.builders

import com.nomanim.bunual.retrofit.services.PhoneModelsService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PhoneModelsApi {

    companion object {

        private val BASE_URL: String = "https://raw.githubusercontent.com/"

        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PhoneModelsService::class.java)
    }
}