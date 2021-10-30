package com.nomanim.bunual.retrofit.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelPlaces(

    @SerializedName("admin_name")
    val city: String,

    @SerializedName("population")
    val population: String = "0" ) : Parcelable

