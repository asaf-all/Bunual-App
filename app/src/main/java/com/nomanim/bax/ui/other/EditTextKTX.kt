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

