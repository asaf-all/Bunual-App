package com.nomanim.bunual.ui.other.ktx

import android.content.DialogInterface
import android.content.Intent
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.crowdfire.cfalertdialog.CFAlertDialog
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.activities.MainActivity

fun Fragment.showDialogOfCloseActivity() {

    val stringOfYesIdentifier = resources.getIdentifier("do_not_want_continue","string",activity?.packageName)
    val stringOfDismissIdentifier = resources.getIdentifier("dismiss","string",activity?.packageName)

    val builder = CFAlertDialog.Builder(requireContext())
        .setTitle(R.string.are_you_sure)
        .setMessage(R.string.changes_will_canceled)
        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
        .addButton(getString(stringOfYesIdentifier),
            ContextCompat.getColor(requireContext(), R.color.white)
            ,ContextCompat.getColor(requireContext(), R.color.red)
            ,CFAlertDialog.CFAlertActionStyle.POSITIVE
            ,CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
            DialogInterface.OnClickListener { dialog, which ->

                val intent = Intent(activity, MainActivity::class.java)
                activity?.finish()
                activity?.startActivity(intent)

            })
        .addButton(getString(stringOfDismissIdentifier),
            ContextCompat.getColor(requireContext(), R.color.white)
            ,ContextCompat.getColor(requireContext(),R.color.green)
            ,CFAlertDialog.CFAlertActionStyle.NEGATIVE
            ,CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
            DialogInterface.OnClickListener { dialog, which ->

                dialog.dismiss()
            })


    builder.show()
}

fun Fragment.showFeaturesBottomSheet(list: ArrayList<String>, textView: TextView, dialogTitle: Int) {

    val builder = CFAlertDialog.Builder(requireContext())
        .setTitle(dialogTitle)
        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
        .setItems(list.toTypedArray()) { dialog, which ->

            textView.text = list[which]
            dialog?.dismiss()
        }

    builder.show()
}