package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentDescriptionBinding
import com.nomanim.bax.databinding.FragmentPriceBinding

class PriceFragment : Fragment() {

    private var _binding: FragmentPriceBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DescriptionFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentPriceBinding.inflate(inflater,container,false)

        return binding.root
    }

}