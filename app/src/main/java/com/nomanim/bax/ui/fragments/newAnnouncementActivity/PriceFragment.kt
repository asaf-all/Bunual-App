package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentPriceBinding

class PriceFragment : Fragment() {

    private var _binding: FragmentPriceBinding? = null
    private val binding get() = _binding!!
    private var radioButtonActive = false
    private var bundle: Bundle? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentPriceBinding.inflate(inflater,container,false)

        binding.maxPrice.visibility = View.INVISIBLE
        binding.minPrice.visibility = View.INVISIBLE
        binding.view3.visibility = View.INVISIBLE

        binding.byAgreementSwitch.setOnClickListener { checkSwitchStatus() }
        binding.priceToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.priceNextToolbarButton.setOnClickListener { navigateToNextFragment(it) }
        binding.priceNextButton.setOnClickListener { navigateToNextFragment(it) }

        return binding.root
    }

    private fun checkSwitchStatus() {

        if (radioButtonActive) {

            radioButtonActive = false
            binding.price.visibility = View.VISIBLE
            binding.maxPrice.visibility = View.INVISIBLE
            binding.minPrice.visibility = View.INVISIBLE
            binding.view3.visibility = View.INVISIBLE

        }else {

            radioButtonActive = true
            binding.price.visibility = View.INVISIBLE
            binding.maxPrice.visibility = View.VISIBLE
            binding.minPrice.visibility = View.VISIBLE
            binding.view3.visibility = View.VISIBLE

        }
    }

    private fun navigateToNextFragment(view: View) {

        bundle = arguments?.getBundle("featuresBundle")

        if (binding.priceEditText.visibility == View.VISIBLE) {

            bundle?.putString("price",binding.priceEditText.text.toString() + " AZN")
            checkPriceEditTextAndNavigate(view)

        }else {

            val minPrice = binding.minPriceEditText.text.toString()
            val maxPrice = binding.maxPriceEditText.text.toString()
            val agreementPrice = "$minPrice-$maxPrice AZN"
            bundle?.putString("price",agreementPrice)
            checkAgreementAndNavigate(view,minPrice,maxPrice)
        }

        bundle?.putBundle("priceBundle",bundle)
    }

    private fun checkPriceEditTextAndNavigate(view: View) {

        if (TextUtils.isEmpty(binding.priceEditText.text.toString())) {

            Snackbar.make(view,getString(R.string.fill_in_all),Snackbar.LENGTH_SHORT).show()
        }else {

            findNavController().navigate(R.id.action_priceFragment_to_userFragment,bundle)
        }
    }

    private fun checkAgreementAndNavigate(view: View, minEditText: String, maxEditText: String) {

        if (TextUtils.isEmpty(minEditText) || TextUtils.isEmpty(maxEditText)) {

            Snackbar.make(view,getString(R.string.fill_in_all),Snackbar.LENGTH_SHORT).show()
        }else {

            findNavController().navigate(R.id.action_priceFragment_to_userFragment,bundle)
        }
    }

}