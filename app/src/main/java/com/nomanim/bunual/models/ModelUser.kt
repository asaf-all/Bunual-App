package com.nomanim.bunual.models

import android.os.Parcelable
import com.nomanim.bunual.api.entity.RegionsResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelUser(
    val name: String,
    val phoneNumber: String,
    val places: RegionsResponse
) : Parcelable