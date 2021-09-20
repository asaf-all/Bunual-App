package com.nomanim.bax.models

import com.google.firebase.Timestamp
import java.util.*

data class ModelAnnouncement( val id: String,
                              val image: String,
                              val description: String,
                              val numberOfViews: String,
                              val time: Timestamp,
                              val numberOfLike: String,
                              val phone: ModelPhone,
                              val user: ModelUser )
