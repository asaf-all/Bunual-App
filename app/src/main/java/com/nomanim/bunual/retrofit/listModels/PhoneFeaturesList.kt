package com.nomanim.bunual.retrofit.listModels

import com.google.gson.annotations.SerializedName
import com.nomanim.bunual.retrofit.models.ModelPhoneFeatures

data class PhoneFeaturesList(

    @SerializedName("RECORDS")
    val modelPhoneModels: List<ModelPhoneFeatures>)