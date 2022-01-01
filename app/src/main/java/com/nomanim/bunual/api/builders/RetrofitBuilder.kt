package com.nomanim.bunual.api.builders

import com.nomanim.bunual.api.RetrofitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {

    companion object {

        private val BASE_URL: String = "https://raw.githubusercontent.com/"

        val service = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }
}