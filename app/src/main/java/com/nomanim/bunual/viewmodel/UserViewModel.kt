package com.nomanim.bunual.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.nomanim.bunual.Constants
import com.nomanim.bunual.api.builders.RxJavaBuilder
import com.nomanim.bunual.api.entity.ModelPlaces
import com.nomanim.bunual.base.BaseViewModel
import com.nomanim.bunual.models.ModelAnnouncement
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class UserViewModel(application: Application) : BaseViewModel(application) {

    private val disposable = CompositeDisposable()
    fun placesLiveData(): LiveData<List<ModelPlaces>> = placesMutableLiveData
    private val placesMutableLiveData = MutableLiveData<List<ModelPlaces>>()

    fun uploadAdsImagesLiveData(): LiveData<ArrayList<String>> = uploadAdsImagesMutableLiveData
    private val uploadAdsImagesMutableLiveData = MutableLiveData<ArrayList<String>>()

    fun uploadAdsLiveData(): LiveData<String> = uploadAdsMutableLiveData
    private val uploadAdsMutableLiveData = MutableLiveData<String>()

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

    //currently only 1 image can be uploaded
    fun uploadAdsImages(storage: FirebaseStorage, fileList: ArrayList<File>) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            val uploadTask = storage.reference.child(Constants.ADS_STORAGE_NAME)
                .child(UUID.randomUUID().toString())
            uploadTask.putFile(Uri.fromFile(fileList[0]))
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val imagesUrl = ArrayList<String>()
                        //imagesUrl.add(uploadTask.downloadUrl.toString())
                        imagesUrl.add(response.result.toString())
                        uploadAdsImagesMutableLiveData.postValue(imagesUrl)
                    } else {
                        errorMutableLiveData.postValue(response.toString())
                    }
                }
        }
    }

    fun uploadAds(firestore: FirebaseFirestore, modelAds: ModelAnnouncement) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection(Constants.ADS_COLLECTION_NAME)
                .add(modelAds).addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        uploadAdsMutableLiveData.postValue("success")
                    } else {
                        errorMutableLiveData.postValue(response.toString())
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}