package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentDescriptionBinding
import com.nomanim.bax.databinding.FragmentHomeBinding

class DescriptionFragment : Fragment() {

    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDescriptionBinding.inflate(inflater,container,false)


        binding.descriptionToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.descriptionNextButton.setOnClickListener { findNavController().navigate(R.id.action_descriptionFragment_to_featuresFragment) }
        binding.descriptionNextToolbarButton.setOnClickListener { findNavController().navigate(R.id.action_descriptionFragment_to_featuresFragment) }

        return binding.root
    }

}