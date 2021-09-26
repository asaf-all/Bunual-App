package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentPicturesBinding
import java.util.*
import kotlin.collections.ArrayList

class PicturesFragment : Fragment() {

    private var _binding: FragmentPicturesBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permission: ActivityResultLauncher<String>
    private val imagesUri = ArrayList<String>()
    private lateinit var firebaseStorage: FirebaseStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentPicturesBinding.inflate(inflater,container,false)

        firebaseStorage = FirebaseStorage.getInstance()
        //uploadImagesToStorage()

        val bundle = arguments?.getBundle("modelsBundle")
        bundle?.putString("pictureUrl","randomUrl")
        bundle?.putBundle("picturesBundle",bundle)
        findNavController().navigate(R.id.action_picturesFragment_to_descriptionFragment,bundle)

        registerLauncher()
        requestPermission()

        binding.selectPhotosImageView.setOnClickListener { imageViewClickAction() }
        binding.imagesToolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        return binding.root
    }

    private fun imageViewClickAction() {

        context?.let {

            if (ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                permission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            }else {

                val intentToGallery = Intent(Intent.ACTION_GET_CONTENT)
                intentToGallery.type = "image/*"
                intentToGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                activityResultLauncher.launch(Intent.createChooser(intentToGallery,"SELECT IMAGE"))
            }
        }
    }

    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->


            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                val intentFromResult = result.data
                if (intentFromResult != null) {
                    if (intentFromResult.clipData != null) {

                            val count = intentFromResult.clipData!!.itemCount
                            for (i in 0 until count) {

                                val uris = intentFromResult.clipData!!.getItemAt(i).uri
                                imagesUri.add(uris.toString())
                            }
                        }
                        else { imagesUri.add(intentFromResult.data.toString()) }

                        if (imagesUri.isEmpty()) { Toast.makeText(requireContext(),getString(R.string.fail),Toast.LENGTH_SHORT).show() }
                        else {

                            navigateToNextFragment()
                        }
                    }
                }

        }
    }

    private fun requestPermission() {

        permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

            if (result) {

                val intentToGallery = Intent(Intent.ACTION_GET_CONTENT)
                intentToGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                intentToGallery.type = "image/*"
                activityResultLauncher.launch(intentToGallery)

            } else {

                context?.let { Toast.makeText(it, resources.getString(R.string.permission_needed), Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun uploadImagesToStorage() {

        val reference = firebaseStorage.reference
        val child = reference.child("Pictures").child(UUID.randomUUID().toString())
        child.putFile("randomUri".toUri()).addOnSuccessListener {

                child.downloadUrl
        }
    }

    private fun navigateToNextFragment() {

        val bundle = arguments?.getBundle("modelsBundle")
        bundle?.putBundle("imagesBundle",bundle)

        Log.e("*********",bundle?.getString("brandsId").toString())

        findNavController().navigate(R.id.action_picturesFragment_to_descriptionFragment,bundle)
    }

}