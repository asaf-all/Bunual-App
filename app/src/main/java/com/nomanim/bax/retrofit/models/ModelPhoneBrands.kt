package com.nomanim.bax.retrofit.models

import com.google.gson.annotations.SerializedName

data class ModelPhoneBrands(

    val id: String,

    @SerializedName("name")
    val brandName: String )