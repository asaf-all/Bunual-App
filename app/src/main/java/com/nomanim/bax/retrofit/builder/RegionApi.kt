package com.nomanim.bax.retrofit.builder

import com.nomanim.bax.retrofit.service.RegionService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegionApi {

    companion object {

        private val BASE_URL: String = "https://raw.githubusercontent.com/"

        fun buildAndCreate(): RegionService {

            val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return builder.create(RegionService::class.java)
        }
    }


}