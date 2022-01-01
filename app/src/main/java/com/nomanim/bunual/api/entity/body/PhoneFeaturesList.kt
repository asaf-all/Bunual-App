package com.nomanim.bunual.api.entity.body

import com.google.gson.annotations.SerializedName
import com.nomanim.bunual.api.entity.ModelPhoneFeatures

data class PhoneFeaturesList(

    @SerializedName("RECORDS")
    val modelPhoneModels: List<ModelPhoneFeatures>)