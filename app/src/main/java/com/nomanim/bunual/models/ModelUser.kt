package com.nomanim.bunual.models

import android.os.Parcelable
import com.nomanim.bunual.retrofit.models.ModelPlaces
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ModelUser( val name: String,
                      val phoneNumber: String,
                      val places: ModelPlaces ) : Parcelable