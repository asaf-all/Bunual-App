package com.nomanim.bax.retrofit.listModels

import com.google.gson.annotations.SerializedName
import com.nomanim.bax.retrofit.models.ModelPhoneBrands

class PhoneBrandsList(

    @SerializedName("RECORDS")
    val modelPhoneBrands: List<ModelPhoneBrands> )
