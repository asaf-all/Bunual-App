package com.nomanim.bax.retrofit.listModels

import com.google.gson.annotations.SerializedName
import com.nomanim.bax.retrofit.models.PhoneBrandName
import com.nomanim.bax.retrofit.models.PhoneModelName

data class PhoneModelsList(

    @SerializedName("RECORDS")
    val phoneModelNames: List<PhoneModelName>)
