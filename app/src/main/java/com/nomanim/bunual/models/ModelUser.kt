package com.nomanim.bunual.models

import com.nomanim.bunual.retrofit.models.ModelPlaces

data class ModelUser( val name: String,
                      val phoneNumber: String,
                      val places: ModelPlaces )