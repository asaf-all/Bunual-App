package com.nomanim.bunual.retrofit.listModels

import com.google.gson.annotations.SerializedName
import com.nomanim.bunual.retrofit.models.ModelPhoneModels

data class PhoneModelsList(

    @SerializedName("RECORDS")
    val modelPhoneModels: List<ModelPhoneModels>)
