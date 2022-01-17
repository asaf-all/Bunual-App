package com.nomanim.bunual.ui.fragments.newadsactivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentPriceBinding
import com.nomanim.bunual.extensions.showDialogOfCloseActivity
import com.nomanim.bunual.models.ModelPhone

class PriceFragment : Fragment() {

    private var _binding: FragmentPriceBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<PriceFragmentArgs>()
    private var sharedPref: SharedPreferences? = null
    private var withAgreement: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPriceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = activity?.getSharedPreferences("newAdsActivity", Context.MODE_PRIVATE)

        pressBackButton()
        if (sharedPref?.getString("price", "") != "By agreement") {
            binding.priceEditText.setText(sharedPref?.getString("price", ""))
        }
        binding.byAgreementSwitch.isChecked = false
        binding.byAgreementSwitch.setOnCheckedChangeListener { _view, isChecked ->
            checkSwitchButtonStatus(isChecked)
        }
        binding.priceToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.priceNextToolbarButton.setOnClickListener {
            navigateToNextFragment(it)
        }
        binding.priceNextButton.setOnClickListener {
            navigateToNextFragment(it)
        }
        binding.priceCancelButton.setOnClickListener {
            showDialogOfCloseActivity()
        }
    }

    private fun checkSwitchButtonStatus(switchButtonStatus: Boolean) {
        if (switchButtonStatus) {
            withAgreement = true
            binding.price.visibility = View.INVISIBLE
        } else {
            withAgreement = false
            binding.price.visibility = View.VISIBLE
        }
    }

    private fun navigateToNextFragment(view: View) {
        val editor = sharedPref?.edit()
        var phone: ModelPhone? = null
        if (!withAgreement) {
            val priceText = binding.priceEditText.text.toString()
            editor?.putString("price", priceText)
            if (TextUtils.isEmpty(priceText)) {
                Snackbar.make(view, getString(R.string.fill_in_all), Snackbar.LENGTH_SHORT).show()
            } else {
                phone = args.announcement.phone.copy(price = priceText)
            }
        } else {
            editor?.putString("price", "By agreement")
            phone = args.announcement.phone.copy(agreementPrice = true)
        }
        editor?.apply()
        phone?.let {
            val announcement = args.announcement.copy(phone = phone)
            val action = PriceFragmentDirections.actionPriceFragmentToUserFragment(announcement)
            findNavController().navigate(action)
        }
    }

    private fun pressBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!withAgreement) {
                        val editor = sharedPref?.edit()
                        editor?.putString("price", binding.priceEditText.text.toString())
                        editor?.apply()
                    }
                    findNavController().navigate(
                        PriceFragmentDirections.actionPriceFragmentToFeaturesFragment(
                            args.announcement
                        )
                    )
                }
            })
    }

}