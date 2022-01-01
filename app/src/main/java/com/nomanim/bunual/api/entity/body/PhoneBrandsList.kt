package com.nomanim.bunual.api.entity.body

import com.google.gson.annotations.SerializedName
import com.nomanim.bunual.api.entity.ModelPhoneBrands

class PhoneBrandsList(

    @SerializedName("RECORDS")
    val modelPhoneBrands: List<ModelPhoneBrands> )
