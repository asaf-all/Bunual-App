package com.nomanim.bunual.base

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.EditText


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

