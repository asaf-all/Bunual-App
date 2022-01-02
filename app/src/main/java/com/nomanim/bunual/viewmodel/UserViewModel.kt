package com.nomanim.bunual.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nomanim.bunual.api.builders.RxJavaBuilder
import com.nomanim.bunual.api.entity.ModelPlaces
import com.nomanim.bunual.api.entity.body.PhoneModelsList
import com.nomanim.bunual.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class UserViewModel(application: Application) : BaseViewModel(application) {

    private val disposable = CompositeDisposable()
    fun placesLiveData(): LiveData<List<ModelPlaces>> = placesMutableLiveData
    private val placesMutableLiveData = MutableLiveData<List<ModelPlaces>>()

    fun getPlaces() {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            disposable.add(
                RxJavaBuilder.service.getPlaces()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<ModelPlaces>>() {
                        override fun onSuccess(list: List<ModelPlaces>) {
                            placesMutableLiveData.postValue(list)
                        }
                        override fun onError(error: Throwable) {
                            errorMutableLiveData.postValue(error.message.toString())
                        }
                    })
            )
        }
    }

    fun uploadAdsImages() {
        CoroutineScope(Dispatchers.IO).launch(handler) {
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}