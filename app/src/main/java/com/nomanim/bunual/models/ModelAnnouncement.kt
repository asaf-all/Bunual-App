package com.nomanim.bunual.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ModelAnnouncement( val id: String,
                              val image: ArrayList<String>,
                              val description: String,
                              val numberOfViews: String,
                              val time: Timestamp,
                              val phone: ModelPhone,
                              val user: ModelUser ) : Parcelable
