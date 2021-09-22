package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentHomeBinding
import com.nomanim.bax.databinding.FragmentImagesBinding
import kotlinx.coroutines.launch

class ImagesFragment : Fragment() {

    private var _binding: FragmentImagesBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ImagesFragmentArgs>()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permission: ActivityResultLauncher<String>
    private val imagesUri = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentImagesBinding.inflate(inflater,container,false)

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
                lifecycleScope.launch { activityResultLauncher.launch(Intent.createChooser(intentToGallery,"SELECT IMAGE")) }
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
                    else { }

                    Toast.makeText(requireContext(),imagesUri.toTypedArray().toString(),Toast.LENGTH_LONG).show()

                    val action = ImagesFragmentDirections.actionİmagesFragmentToDescriptionFragment(

                        intentFromResult.data.toString(), imagesUri.toTypedArray(), args.brandName,args.modelName)

                    findNavController().navigate(action)
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

}