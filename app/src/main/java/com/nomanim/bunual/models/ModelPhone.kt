package com.nomanim.bunual.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ModelPhone( val brand: String,
                       val model: String,
                       val price: String,
                       val color: String,
                       val storage: String,
                       val ram: String,
                       val currentStatus: String,
                       val delivery: String,
                       val agreementPrice: Boolean ) : Parcelable
