package com.nomanim.bunual.retrofit.services

import com.nomanim.bunual.retrofit.models.ModelPlaces
import io.reactivex.Single
import retrofit2.http.GET

interface PlacesService {

    @GET("AsafHacioglu/bax-api-collection/master/az.json")

    fun getData() : Single<List<ModelPlaces>>
}