package com.nomanim.bunual.retrofit.models

import com.google.gson.annotations.SerializedName


data class ModelPlaces(

    @SerializedName("admin_name")
    val city: String,

    //@SerializedName("population")
    val population: String = "0" )

