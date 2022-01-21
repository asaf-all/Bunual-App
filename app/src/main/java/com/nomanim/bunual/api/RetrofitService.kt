package com.nomanim.bunual.api

import com.nomanim.bunual.api.entity.BrandsResponse
import com.nomanim.bunual.api.entity.ModelsResponse
import com.nomanim.bunual.api.entity.RegionsResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("iamasaf-profile/bunual-api-collection/master/brands.json")
    fun getPhoneBrands() : Call<BrandsResponse>  //with retrofit

    @GET("iamasaf-profile/bunual-api-collection/master/devices.json")
    fun getPhoneModels() : Single<ModelsResponse>  //with rxjava (single)

    @GET("iamasaf-profile/bax-api-collection/master/az.json")
    fun getPlaces() : Single<List<RegionsResponse>>
}