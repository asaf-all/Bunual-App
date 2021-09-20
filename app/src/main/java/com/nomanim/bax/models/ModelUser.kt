package com.nomanim.bax.models

import com.nomanim.bax.retrofit.models.ModelPlace

data class ModelUser( val name: String,
                      val phoneNumber: String,
                      val place: ModelPlace )