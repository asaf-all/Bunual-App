package com.nomanim.bax.ui.other

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.EditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.models.ModelPhone
import com.nomanim.bax.models.ModelUser
import com.nomanim.bax.retrofit.models.ModelPlaces


@SuppressLint("ClickableViewAccessibility")
fun EditText.clearTextWhenClickClear() {

    this.setOnTouchListener { _, event ->
        val DRAWABLE_RIGHT = 2
        //val DRAWABLE_LEFT = 0
        //val DRAWABLE_TOP = 1
        //val DRAWABLE_BOTTOM = 3

        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (this.right - this.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {

                this.setText("")
            }
        }
        false
    }
}

fun ArrayList<ModelAnnouncement>.getDataFromFireStore(fireStore: FirebaseFirestore, collectionName: String?, value: QuerySnapshot?): ArrayList<ModelAnnouncement> {

    val list: ArrayList<ModelAnnouncement> = ArrayList()

    if (value != null) {

        for (doc in value.documents) {

            val phone = doc.get("phone") as Map<*, *>
            val brand = phone["brand"] as String
            val model = phone["model"] as String
            val price = phone["price"] as String
            val color = phone["color"] as String
            val storage = phone["storage"] as String
            val ram = phone["ram"] as String
            val currentStatus = phone["currentStatus"] as String
            val delivery = phone["delivery"] as String
            val agreementPrice = phone["agreementPrice"] as Boolean

            val user = doc.get("user") as Map<*, *>
            val place = user["place"] as Map<*, *>
            val city = place["city"] as String
            val region = place["region"] as String
            val population = place["population"] as String

            val userName = user["name"] as String
            val phoneNumber = user["phoneNumber"] as String

            val id = doc.id
            collectionName?.let { fireStore.collection(collectionName).document(id).update("id",id) }

            val image = doc.get("image") as String
            val description = doc.get("description") as String
            val numberOfViews = doc.get("numberOfViews") as String
            val time = doc.get("time") as Timestamp
            val numberOfLike = doc.get("numberOfLike") as String

            val phoneModel = ModelPhone(brand,model,price,color,storage,ram,currentStatus,delivery,agreementPrice)
            val placeModel = ModelPlaces(city,region,population)
            val userModel = ModelUser(userName,phoneNumber,placeModel)
            val announcement = ModelAnnouncement(id,image,description,numberOfViews,time,numberOfLike,phoneModel,userModel)

            list.add(announcement)
        }

        this.addAll(list)
    }
    return this
}
