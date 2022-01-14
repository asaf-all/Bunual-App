package com.nomanim.bunual.ui.fragments.mainactivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.activities.NewAdsActivity
import com.nomanim.bunual.base.BaseFragment
import com.nomanim.bunual.databinding.FragmentNewAdsBinding

class NewAdsFragment : BaseFragment() {

    private var _binding: FragmentNewAdsBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentNewAdsBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()

        binding.newAdsImageView.visibility = View.INVISIBLE
        binding.newAdsTextView.visibility = View.INVISIBLE
        binding.buttonInNewAdsFragment.visibility = View.INVISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateSomeFieldOfSharedPreferences()
    }

    private fun updateSomeFieldOfSharedPreferences() {
        val sharedPref = activity?.getSharedPreferences("sharedPrefInNewAdsActivity", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putString("description","")
        editor?.putString("storageCapacity",getString(R.string.choose_phone_storage))
        editor?.putString("ramCapacity",getString(R.string.choose_phone_ram))
        editor?.putString("color",getString(R.string.choose_phone_color))
        editor?.putString("price","")
        editor?.apply()

        intentActivityOfNewAds()
    }

    private fun intentActivityOfNewAds() {
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.INVISIBLE
        val intent = Intent(activity, NewAdsActivity::class.java)
        activity?.finish()
        activity?.startActivity(intent)
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}