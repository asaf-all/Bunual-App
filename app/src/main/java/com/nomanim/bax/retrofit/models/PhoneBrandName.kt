package com.nomanim.bax.retrofit.models

import com.google.gson.annotations.SerializedName

data class PhoneBrandName(

    val id: String,

    @SerializedName("name")
    val brandName: String )