package com.nomanim.bunual.base

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.activities.AdsDetailsActivity
import com.nomanim.bunual.ui.activities.MainActivity
import com.nomanim.bunual.ui.activities.NewAdsActivity
import java.util.zip.Inflater

open class BaseFragment : Fragment() {

    var hasInitializedRootView = false
    private var rootView: View? = null

    val mMainActivity: MainActivity by lazy {
        requireActivity() as MainActivity
    }

    val mAdsDetailsActivity: AdsDetailsActivity by lazy {
        requireActivity() as AdsDetailsActivity
    }

    val mNewAdsActivity: NewAdsActivity by lazy {
        requireActivity() as NewAdsActivity
    }

    protected fun getPersistentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
        layout: Int
    ): View? {
        if (rootView == null) {
            rootView = inflater?.inflate(layout, container, false)
        } else {
            (rootView?.parent as? ViewGroup)?.removeView(rootView)
        }
        return rootView
    }

    protected fun showToastMessage(text: String, lengthIsLong: Boolean = false) {
        if (lengthIsLong) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun showDialog(
        title: String? = "",
        message: String?,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        onYesClickListener: DialogInterface.OnClickListener?
        = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() },
        onNoClickListener: DialogInterface.OnClickListener?
        = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }
    ) {
        val dialog = AlertDialog.Builder(requireContext())
            .apply {
                setTitle(title)
                setMessage(message ?: "")
                if (positiveButtonText == null) {
                    setPositiveButton(getString(R.string.ok), onYesClickListener)
                } else {
                    setPositiveButton(positiveButtonText, onYesClickListener)
                }
                if (negativeButtonText != null) {
                    setNegativeButton(negativeButtonText, onNoClickListener)
                }
            }.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
    }


}