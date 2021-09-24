package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentDescriptionBinding

class DescriptionFragment : Fragment() {

    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DescriptionFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDescriptionBinding.inflate(inflater,container,false)

        binding.descriptionToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.descriptionNextButton.setOnClickListener { findNavController().navigate(R.id.action_descriptionFragment_to_featuresFragment) }
        binding.descriptionNextToolbarButton.setOnClickListener { findNavController().navigate(R.id.action_descriptionFragment_to_featuresFragment) }

        binding.desctiptionEditText.text

        val action = DescriptionFragmentDirections.actionDescriptionFragmentToFeaturesFragment(

            args.brandName,args.modelName,args.imagesUri,binding.desctiptionEditText.text.toString())

        findNavController().navigate(action)

        return binding.root
    }

}