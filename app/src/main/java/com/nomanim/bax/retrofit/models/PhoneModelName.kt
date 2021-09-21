package com.nomanim.bax.retrofit.models

import com.google.gson.annotations.SerializedName

data class PhoneModelName(

    val id:String,
    val chipset: String,
    val storage:String,
    val ram: String,
    val os: String,
    val body: String,

    @SerializedName("picture")
    val modelImage: String,

    @SerializedName("brand_id")
    val brandId: String,

    @SerializedName("name")
    val modelName: String,

    @SerializedName("released_at")
    val releasedTime: String,

    @SerializedName("display_size")
    val displaySize:String,

    @SerializedName("display_resolution")
    val displayResolution:String,

    @SerializedName("camera_pixels")
    val cameraPixel:String,

    @SerializedName("video_pixels")
    val videoPixel:String,

    @SerializedName("battery_size")
    val batterySize:String,

    @SerializedName("battery_type")
    val batteryType:String )