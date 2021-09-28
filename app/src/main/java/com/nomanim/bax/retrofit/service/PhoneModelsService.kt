package com.nomanim.bax.retrofit.service

import com.nomanim.bax.retrofit.listModels.PhoneModelsList
import io.reactivex.Observable
import retrofit2.http.GET

interface PhoneModelsService {

    @GET("ilyasozkurt/mobilephone-brands-and-models/master/devices.json")
    fun getData() : Observable<PhoneModelsList>
}