package com.nomanim.bax.retrofit.models

import com.google.gson.annotations.SerializedName

data class ModelPhoneModels(

    val id:String,

    @SerializedName("picture")
    val modelImage: String,

    @SerializedName("brand_id")
    val brandId: String,

    @SerializedName("name")
    val modelName: String )