package com.nomanim.bax.ui.other

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.EditText


@SuppressLint("ClickableViewAccessibility")
class ClearEditTextButton(editText: EditText) {

    init {

        editText.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            /*val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_BOTTOM = 3*/

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (editText.right - editText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    editText.setText("")
                    true
                }
            }
            false
        }
    }
}