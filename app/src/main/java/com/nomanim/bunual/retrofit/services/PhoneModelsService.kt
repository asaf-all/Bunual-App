package com.nomanim.bunual.retrofit.services

import com.nomanim.bunual.retrofit.listModels.PhoneModelsList
import io.reactivex.Observable
import retrofit2.http.GET

interface PhoneModelsService {

    @GET("AsafHacioglu/bax-api-collection/master/devices.json")
    fun getData() : Observable<PhoneModelsList>
}