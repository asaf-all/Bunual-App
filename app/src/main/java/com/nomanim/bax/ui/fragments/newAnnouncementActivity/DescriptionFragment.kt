package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentDescriptionBinding

class DescriptionFragment : Fragment() {

    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDescriptionBinding.inflate(inflater,container,false)

        binding.descriptionToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.descriptionNextButton.setOnClickListener { checkEditTextAndNavigate(it) }
        binding.descriptionNextToolbarButton.setOnClickListener { checkEditTextAndNavigate(it) }

        return binding.root
    }

    private fun checkEditTextAndNavigate(view: View) {

        if (TextUtils.isEmpty(binding.descriptionEditText.text.toString())) {

            Snackbar.make(view,resources.getString(R.string.fill_in_all), Snackbar.LENGTH_SHORT).show()

        }else { navigateToFeaturesFragment() }

    }

    private fun navigateToFeaturesFragment() {

        val bundle = arguments?.getBundle("picturesBundle")
        bundle?.putString("description",binding.descriptionEditText.text.toString())
        bundle?.putBundle("descriptionBundle",bundle)

        findNavController().navigate(R.id.action_descriptionFragment_to_featuresFragment,bundle)
    }

}