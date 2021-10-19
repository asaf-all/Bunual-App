package com.nomanim.bunual.retrofit.builders

import com.nomanim.bunual.retrofit.services.SimpleDataService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SimpleDataApi {

    companion object {

        private val BASE_URL: String = "https://raw.githubusercontent.com/"

        val builder: SimpleDataService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SimpleDataService::class.java)
    }
}