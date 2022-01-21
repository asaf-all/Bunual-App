package com.nomanim.bunual.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.api.builders.RetrofitBuilder
import com.nomanim.bunual.api.builders.RxJavaBuilder
import com.nomanim.bunual.api.entity.BrandsResponse
import com.nomanim.bunual.api.entity.ModelsResponse
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

    fun brandsLiveData(): LiveData<BrandsResponse> = brandsMutableLiveData
    private val brandsMutableLiveData = MutableLiveData<BrandsResponse>()

    private val disposable = CompositeDisposable()
    fun modelsLiveData(): LiveData<ModelsResponse> = modelsMutableLiveData
    private val modelsMutableLiveData = MutableLiveData<ModelsResponse>()

    fun getApiVersionCode(firestore: FirebaseFirestore) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection("Important Data")
                .document("api_version").get()
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
            request.enqueue(object : Callback<BrandsResponse> {
                override fun onResponse(
                    call: Call<BrandsResponse>,
                    response: Response<BrandsResponse>?
                ) {
                    if (response != null) {
                        if (response.isSuccessful) {
                            brandsMutableLiveData.postValue(response.body())
                        } else {
                            errorMutableLiveData.postValue(response.errorBody().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<BrandsResponse>, error: Throwable) {
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
                    .subscribeWith(object : DisposableSingleObserver<ModelsResponse>() {
                        override fun onSuccess(response: ModelsResponse) {
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