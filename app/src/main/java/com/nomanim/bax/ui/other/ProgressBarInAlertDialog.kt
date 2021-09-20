package com.nomanim.bax.ui.other

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.nomanim.bax.databinding.LayoutCustomProgessbarInAlertdialogBinding

class ProgressBarInAlertDialog(private val context: Context) {

    private var _binding: LayoutCustomProgessbarInAlertdialogBinding? = null
    private val binding get() = _binding
    private lateinit var alertDialog: AlertDialog

    fun showAlertDialog() {

        _binding = LayoutCustomProgessbarInAlertdialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(context)
        builder.setView(binding?.root)
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog.show()

    }

    fun closeAlertDialog() {

        alertDialog.dismiss()
    }

}