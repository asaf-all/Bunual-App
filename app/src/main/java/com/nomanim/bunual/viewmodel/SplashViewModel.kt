package com.nomanim.bunual.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.api.builders.RetrofitBuilder
import com.nomanim.bunual.api.builders.RxJavaBuilder
import com.nomanim.bunual.api.entity.body.PhoneBrandsList
import com.nomanim.bunual.api.entity.body.PhoneModelsList
import com.nomanim.bunual.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashViewModel(application: Application) : BaseViewModel(application) {

    fun apiVersionLiveData(): LiveData<DocumentSnapshot> = apiVersionMutableLiveData
    private val apiVersionMutableLiveData = MutableLiveData<DocumentSnapshot>()

    fun brandsLiveData(): LiveData<PhoneBrandsList> = brandsMutableLiveData
    private val brandsMutableLiveData = MutableLiveData<PhoneBrandsList>()

    private val disposable = CompositeDisposable()
    fun modelsLiveData(): LiveData<PhoneModelsList> = modelsMutableLiveData
    private val modelsMutableLiveData = MutableLiveData<PhoneModelsList>()

    fun getApiVersionCode(firestore: FirebaseFirestore) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection("Important Data")
                .document("api_version")
                .get()
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val document = response.result
                        if (document != null) {
                            apiVersionMutableLiveData.postValue(document!!)
                        }
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }

    fun getPhoneBrands() {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            val request = RetrofitBuilder.service.getPhoneBrands()
            request.enqueue(object : Callback<PhoneBrandsList> {
                override fun onResponse(
                    call: Call<PhoneBrandsList>,
                    response: Response<PhoneBrandsList>?
                ) {
                    if (response != null) {
                        if (response.isSuccessful) {
                            brandsMutableLiveData.postValue(response.body())
                        } else {
                            errorMutableLiveData.postValue(response.errorBody().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<PhoneBrandsList>, error: Throwable) {
                    errorMutableLiveData.postValue(error.message.toString())
                }
            })
        }
    }

    fun getPhoneModels() {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            disposable.add(
                RxJavaBuilder.service.getPhoneModels()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<PhoneModelsList>() {
                        override fun onSuccess(response: PhoneModelsList) {
                            modelsMutableLiveData.postValue(response)
                        }

                        override fun onError(error: Throwable) {
                            errorMutableLiveData.postValue(error.message.toString())
                        }
                    })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}