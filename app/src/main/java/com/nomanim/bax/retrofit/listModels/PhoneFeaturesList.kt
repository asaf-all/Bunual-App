package com.nomanim.bax.retrofit.listModels

import com.google.gson.annotations.SerializedName
import com.nomanim.bax.retrofit.models.ModelPhoneFeatures

data class PhoneFeaturesList(

    @SerializedName("RECORDS")
    val modelPhoneModels: List<ModelPhoneFeatures>)