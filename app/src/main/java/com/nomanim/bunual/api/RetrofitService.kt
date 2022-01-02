package com.nomanim.bunual.api

import com.nomanim.bunual.api.entity.ModelPlaces
import com.nomanim.bunual.api.entity.ModelSimpleData
import com.nomanim.bunual.api.entity.body.PhoneBrandsList
import com.nomanim.bunual.api.entity.body.PhoneModelsList
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("iamasaf-profile/bunual-api-collection/master/brands.json")
    fun getPhoneBrands() : Call<PhoneBrandsList>  //with retrofit

    @GET("iamasaf-profile/bunual-api-collection/master/devices.json")
    fun getPhoneModels() : Single<PhoneModelsList>  //with rxjava (single)

    @GET("iamasaf-profile/bax-api-collection/master/az.json")
    fun getPlaces() : Single<List<ModelPlaces>>
}