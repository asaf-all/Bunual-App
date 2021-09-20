package com.nomanim.bax.retrofit.listModels

import com.google.gson.annotations.SerializedName
import com.nomanim.bax.retrofit.models.PhoneBrandName

class PhoneBrandsList(

    @SerializedName("RECORDS")
    val phoneBrandNames: List<PhoneBrandName> )
