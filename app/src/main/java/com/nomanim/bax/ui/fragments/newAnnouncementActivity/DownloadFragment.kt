package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nomanim.bax.databinding.FragmentDownloadBinding
import com.nomanim.bax.ui.other.ProgressBarInAlertDialog

class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDownloadBinding.inflate(inflater)

        context?.let { ProgressBarInAlertDialog(it).showAlertDialog() }

        val bundle = arguments?.getBundle("priceBundle")


        return binding.root
    }
}