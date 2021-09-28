package com.nomanim.bax.retrofit.service

import com.nomanim.bax.retrofit.models.ModelPlaces
import io.reactivex.Single
import retrofit2.http.GET

interface PlacesService {

    @GET("AsafHacioglu/bax-api-collection/c730ff5e7182b2ba1892f1947a6ba60c9aa28120/az.json?token=ASQRT2DPY4YR63IMTYTQ5YLBKLPA2")

    fun getData() : Single<List<ModelPlaces>>
}