package com.nomanim.bunual.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val handler = CoroutineExceptionHandler { _, exception ->
        errorMutableLiveData.postValue(exception.message)
    }

    fun errorLiveData(): LiveData<String> = errorMutableLiveData
    val errorMutableLiveData = MutableLiveData<String>()
}