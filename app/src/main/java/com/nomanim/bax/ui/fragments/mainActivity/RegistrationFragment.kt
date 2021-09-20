package com.nomanim.bax.ui.fragments.mainActivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.ActivityNavigator
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentHomeBinding
import com.nomanim.bax.databinding.FragmentRegistrationBinding
import com.nomanim.bax.ui.activities.MainActivity
import com.nomanim.bax.ui.activities.RegistrationActivity

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentRegistrationBinding.inflate(inflater,container,false)

        binding.createAccountButton.setOnClickListener {

            requireActivity().startActivity(Intent(requireActivity(),RegistrationActivity::class.java)) }

        binding.openAccountButton.setOnClickListener {

            requireActivity().startActivity(Intent(requireActivity(),RegistrationActivity::class.java)) }

        return binding.root
    }

}