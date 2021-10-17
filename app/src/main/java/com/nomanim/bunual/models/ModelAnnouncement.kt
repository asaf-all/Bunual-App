package com.nomanim.bunual.models

import com.google.firebase.Timestamp

data class ModelAnnouncement( val id: String,
                              val image: ArrayList<String>,
                              val description: String,
                              val numberOfViews: String,
                              val time: Timestamp,
                              val numberOfLike: String,
                              val phone: ModelPhone,
                              val user: ModelUser )
