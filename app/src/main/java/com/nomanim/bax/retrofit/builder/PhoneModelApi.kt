package com.nomanim.bax.retrofit.builder

import com.nomanim.bax.retrofit.service.PhoneModelService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhoneModelApi {

    companion object {

        private val BASE_URL: String = "https://raw.githubusercontent.com/"

        fun buildAndCreate(): PhoneModelService {

            val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return builder.create(PhoneModelService::class.java)
        }
    }
}