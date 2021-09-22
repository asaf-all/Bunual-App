package com.nomanim.bax.models

import com.nomanim.bax.retrofit.models.ModelPlaces

data class ModelUser( val name: String,
                      val phoneNumber: String,
                      val places: ModelPlaces )