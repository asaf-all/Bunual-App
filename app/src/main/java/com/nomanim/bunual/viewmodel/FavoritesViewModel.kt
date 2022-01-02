package com.nomanim.bunual.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.R
import com.nomanim.bunual.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : BaseViewModel(application) {

    fun idsLiveData(): LiveData<QuerySnapshot> = idsMutableLiveData
    private val idsMutableLiveData = MutableLiveData<QuerySnapshot>()

    fun favoritesLiveData(): LiveData<QuerySnapshot> = favoritesMutableLiveData
    private val favoritesMutableLiveData = MutableLiveData<QuerySnapshot>()

    fun getIds(firestore: FirebaseFirestore, phoneNumber: String) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection(phoneNumber).get()
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val documents = response.result
                        if (documents != null) {
                            idsMutableLiveData.postValue(documents!!)
                        }
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }

    fun getFavorites(firestore: FirebaseFirestore, favoritesPhones: ArrayList<String>) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection("All Announcements")
                .whereIn("id", favoritesPhones).get()
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val documents = response.result
                        if (documents != null) {
                            favoritesMutableLiveData.postValue(documents!!)
                        }
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }
}