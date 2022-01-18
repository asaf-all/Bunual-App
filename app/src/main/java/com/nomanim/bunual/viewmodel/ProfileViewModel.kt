package com.nomanim.bunual.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.Constants
import com.nomanim.bunual.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : BaseViewModel(application) {

    fun userAdsLiveData(): LiveData<QuerySnapshot> = userAdsMutableLiveData
    private val userAdsMutableLiveData = MutableLiveData<QuerySnapshot>()

    fun moreLiveData(): LiveData<QuerySnapshot> = moreMutableLiveData
    private val moreMutableLiveData = MutableLiveData<QuerySnapshot>()


    fun getUserAds(firestore: FirebaseFirestore, phoneNumber: String, numberOfAds: Long) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection(Constants.ADS_COLLECTION_NAME)
                .whereEqualTo("user_token", phoneNumber)
                .orderBy("time", Query.Direction.ASCENDING)
                .limit(numberOfAds)
                .get()
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val documents = response.result
                        if (documents != null) {
                            userAdsMutableLiveData.postValue(documents!!)
                        }
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }

    fun getMoreAds(firestore: FirebaseFirestore, lastValue: QuerySnapshot, numberOfAds: Long) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection(Constants.ADS_COLLECTION_NAME)
                .orderBy("time", Query.Direction.ASCENDING)
                .startAfter(lastValue.documents[lastValue.size() - 1])
                .limit(numberOfAds).get()
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val documents = response.result
                        if (documents != null) {
                            moreMutableLiveData.postValue(documents!!)
                        }
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }
}