package com.nomanim.bax.retrofit.builder

import com.nomanim.bax.retrofit.listModels.PhoneModelsList
import com.nomanim.bax.retrofit.service.PhoneModelService
import io.reactivex.Observable
import io.reactivex.rxjava3.core.Observer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PhoneModelApi {

    companion object {

        private val BASE_URL: String = "https://raw.githubusercontent.com/"

        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PhoneModelService::class.java)
    }
}