package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.nomanim.bax.databinding.FragmentDownloadBinding
import com.nomanim.bax.ui.other.ProgressBarInAlertDialog
import java.util.*

class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseStorage: FirebaseStorage
    private val downloadUrlList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDownloadBinding.inflate(inflater)

        context?.let { ProgressBarInAlertDialog(it).showAlertDialog() }

        val bundle = arguments?.getBundle("priceBundle")


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
}