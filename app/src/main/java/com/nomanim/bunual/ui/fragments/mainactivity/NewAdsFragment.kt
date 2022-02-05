package com.nomanim.bunual.ui.fragments.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.activities.NewAdsActivity
import com.nomanim.bunual.base.BaseFragment
import com.nomanim.bunual.databinding.FragmentNewAdsBinding

class NewAdsFragment : BaseFragment() {

    private var _binding: FragmentNewAdsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        if (auth.currentUser != null) {
            binding.imgFolder.visibility = View.INVISIBLE
            binding.txtText.visibility = View.INVISIBLE
            binding.btnStart.visibility = View.INVISIBLE
            intentToNewAdsActivity()
        }

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_newAnnouncementFragment_to_profileFragment)
        }

        binding.txtDiscoverApp.setOnClickListener {
            intentToNewAdsActivity()
        }
    }

    private fun intentToNewAdsActivity() {
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility =
            View.INVISIBLE
        val intent = Intent(activity, NewAdsActivity::class.java)
        activity?.finish()
        activity?.startActivity(intent)
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}