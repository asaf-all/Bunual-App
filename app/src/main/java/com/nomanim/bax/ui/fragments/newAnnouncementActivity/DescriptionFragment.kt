package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentDescriptionBinding
import com.nomanim.bax.room.database.RoomDB
import com.nomanim.bax.ui.other.BaseCoroutineScope
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch

class DescriptionFragment : BaseCoroutineScope() {

    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDescriptionBinding.inflate(inflater,container,false)

        binding.descriptionToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.descriptionNextButton.setOnClickListener { checkEditTextAndNavigate(it) }
        binding.descriptionNextToolbarButton.setOnClickListener { checkEditTextAndNavigate(it) }

        launch {

            val room = RoomDB(requireContext()).getDataFromRoom().getImagesUriFromDb()
            Toast.makeText(requireContext(),room.toString(),Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    private fun checkEditTextAndNavigate(view: View) {

        if (TextUtils.isEmpty(binding.descriptionEditText.text.toString())) {

            Snackbar.make(view,resources.getString(R.string.fill_in_all), Snackbar.LENGTH_SHORT).show()

        }else { navigateToNextFragment() }

    }

    private fun navigateToNextFragment() {

        val bundle = arguments?.getBundle("picturesBundle")
        bundle?.putString("description",binding.descriptionEditText.text.toString())
        bundle?.putBundle("descriptionBundle",bundle)
        findNavController().navigate(R.id.action_descriptionFragment_to_featuresFragment,bundle)
    }

}