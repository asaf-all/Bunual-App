package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.nomanim.bax.databinding.FragmentDownloadBinding
import java.util.*

class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseStorage: FirebaseStorage
    private val downloadUrlList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDownloadBinding.inflate(inflater)

        loadingProgressBarInDialog()

        return binding.root
    }

    private fun uploadImagesToStorage(imagesList: List<Uri>) {

        val reference = firebaseStorage.reference

        val child = reference.child("Pictures").child(UUID.randomUUID().toString())
        child.putFile(imagesList[0]).addOnSuccessListener {

            downloadUrlList.add(child.downloadUrl.toString())
            //navigateToNextFragment()
        }

    }

    private fun loadingProgressBarInDialog() {

        KProgressHUD.create(requireContext())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("Downloading data")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.3f)
            .show()
    }
}