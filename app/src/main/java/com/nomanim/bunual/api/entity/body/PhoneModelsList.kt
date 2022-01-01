package com.nomanim.bunual.api.entity.body

import com.google.gson.annotations.SerializedName
import com.nomanim.bunual.api.entity.ModelPhoneModels

data class PhoneModelsList(

    @SerializedName("RECORDS")
    val modelPhoneModels: List<ModelPhoneModels>)
