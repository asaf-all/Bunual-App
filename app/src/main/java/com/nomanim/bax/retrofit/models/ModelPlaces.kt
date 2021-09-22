package com.nomanim.bax.retrofit.models

import com.google.gson.annotations.SerializedName


data class ModelPlaces(

    @SerializedName("admin_name")
    val city: String,

    @SerializedName("city")
    val region: String,

    //@SerializedName("population")
    val population: String )
