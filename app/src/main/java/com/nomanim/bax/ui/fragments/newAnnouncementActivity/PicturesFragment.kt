package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentPicturesBinding
import com.nomanim.bax.models.ModelImages
import com.nomanim.bax.room.database.RoomDB
import com.nomanim.bax.ui.other.BaseCoroutineScope
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.coroutines.launch


class PicturesFragment : BaseCoroutineScope() {

    private var _binding: FragmentPicturesBinding? = null
    private val binding get() = _binding!!
    private val imagesUrl = ArrayList<Uri>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentPicturesBinding.inflate(inflater)

        openGallery()

        return binding.root
    }

    private fun openGallery() {

        TedImagePicker.with(requireContext())
            .title(R.string.select_phone_image)
            .backButton(R.drawable.back)
            .buttonText(R.string.next)
            .buttonBackground(R.color.main)
            .mediaType(MediaType.IMAGE)
            .startMultiImage { imagesUri ->

                val list = ArrayList<ModelImages>()

                for (element in imagesUri) {

                    val model = ModelImages(element.toString())
                    list.add(model)
                }

                saveImagesUrlAtRoom(list,imagesUri)
            }
    }

    private fun saveImagesUrlAtRoom(list: ArrayList<ModelImages>, imagesUri: List<Uri>) {

        launch {

            val database = RoomDB(requireContext()).getDataFromRoom()
            database.deleteImagesUri()
            database.insertImagesUri(*list.toTypedArray())
            imagesUrl.addAll(imagesUri)
            navigateToNextFragment()
        }
    }

    private fun navigateToNextFragment() {

        findNavController().navigate(R.id.action_picturesFragment_to_descriptionFragment)
    }

}