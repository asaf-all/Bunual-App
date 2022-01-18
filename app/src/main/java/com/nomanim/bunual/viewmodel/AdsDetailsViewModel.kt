package com.nomanim.bunual.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.Constants
import com.nomanim.bunual.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdsDetailsViewModel(application: Application) : BaseViewModel(application) {

    fun currentAdsLiveData(): LiveData<DocumentSnapshot> = currentAdsMutableLiveData
    private val currentAdsMutableLiveData = MutableLiveData<DocumentSnapshot>()

    fun addToFavoritesLiveData(): LiveData<String> = addToFavoritesMutableLiveData
    private val addToFavoritesMutableLiveData = MutableLiveData<String>()

    fun deleteFromFavoritesLiveData(): LiveData<String> = deleteFromFavoritesMutableLiveData
    private val deleteFromFavoritesMutableLiveData = MutableLiveData<String>()

    fun getCurrentAds(firestore: FirebaseFirestore, currentAdsId: String) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection(Constants.ADS_COLLECTION_NAME)
                .document(currentAdsId).get()
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val documents = response.result
                        if (documents != null) {
                            currentAdsMutableLiveData.postValue(documents!!)
                        }
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }

    fun addToFavorites(firestore: FirebaseFirestore, userPhoneNumber: String, currentAdsId: String) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            val hashMap = HashMap<String, String>()
            hashMap["originalAdsId"] = currentAdsId
            firestore.collection(userPhoneNumber).document(currentAdsId)
                .set(hashMap).addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        addToFavoritesMutableLiveData.postValue("success")
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }

    fun deleteFromFavorites(firestore: FirebaseFirestore, userPhoneNumber: String, currentAdsId: String) {
        CoroutineScope(Dispatchers.IO).launch(handler) {

            firestore.collection(userPhoneNumber).document(currentAdsId)
                .delete().addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        deleteFromFavoritesMutableLiveData.postValue("success")
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }
}