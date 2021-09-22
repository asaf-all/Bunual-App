package com.nomanim.bax.retrofit.listModels

import com.google.gson.annotations.SerializedName
import com.nomanim.bax.retrofit.models.ModelPhoneModels

data class PhoneModelsList(

    @SerializedName("RECORDS")
    val modelPhoneModels: List<ModelPhoneModels>)
