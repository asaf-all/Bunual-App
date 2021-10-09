package com.nomanim.bax.retrofit.services

import com.nomanim.bax.retrofit.models.ModelPlaces
import io.reactivex.Single
import retrofit2.http.GET

interface PlacesService {

    @GET("AsafHacioglu/bax-api-collection/master/az.json")

    fun getData() : Single<List<ModelPlaces>>
}