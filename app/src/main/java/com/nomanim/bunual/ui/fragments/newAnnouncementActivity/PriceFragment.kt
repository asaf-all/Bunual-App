package com.nomanim.bunual.ui.fragments.newAnnouncementActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentPriceBinding
import com.nomanim.bunual.ui.other.ktx.showDialogOfCloseActivity

class PriceFragment : Fragment() {

    private var _binding: FragmentPriceBinding? = null
    private val binding get() = _binding!!
    private var sharedPref: SharedPreferences? = null
    private var withAgreement: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentPriceBinding.inflate(inflater,container,false)
        sharedPref = activity?.getSharedPreferences("sharedPrefInNewAdsActivity",Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pressBackButton()

        if (sharedPref?.getString("price","") != getString(R.string.by_agreement)) {

            binding.priceEditText.setText(sharedPref?.getString("price",""))
        }

        binding.byAgreementSwitch.isChecked = false
        binding.byAgreementSwitch.setOnCheckedChangeListener { _view, isChecked -> checkSwitchButtonStatus(isChecked) }
        binding.priceToolbar.setNavigationOnClickListener { navigateToPreviousFragment() }
        binding.priceNextToolbarButton.setOnClickListener { navigateToNextFragment(it) }
        binding.priceNextButton.setOnClickListener { navigateToNextFragment(it) }
        binding.priceCancelButton.setOnClickListener { showDialogOfCloseActivity() }
    }

    private fun checkSwitchButtonStatus(switchButtonStatus: Boolean) {

        if (switchButtonStatus) {

            withAgreement = true
            binding.price.visibility = View.INVISIBLE

        }else {

            withAgreement = false
            binding.price.visibility = View.VISIBLE
        }
    }

    private fun navigateToNextFragment(view: View) {

        val editor = sharedPref?.edit()

        if (!withAgreement) {

            editor?.putString("price",binding.priceEditText.text.toString())
            checkPriceEditTextAndNavigate(view)

        }else {

            editor?.putString("price",getString(R.string.by_agreement))
            findNavController().navigate(R.id.action_priceFragment_to_userFragment)
        }

        editor?.apply()
    }

    private fun checkPriceEditTextAndNavigate(view: View) {

        if (TextUtils.isEmpty(binding.priceEditText.text.toString())) {

            Snackbar.make(view,getString(R.string.fill_in_all),Snackbar.LENGTH_SHORT).show()

        }else {

            findNavController().navigate(R.id.action_priceFragment_to_userFragment)
        }
    }

    private fun navigateToPreviousFragment() {

        if (!withAgreement) {

            val editor = sharedPref?.edit()
            editor?.putString("price",binding.priceEditText.text.toString())
            editor?.apply()
        }
        findNavController().navigate(R.id.action_priceFragment_to_featuresFragment)
    }

    private fun pressBackButton() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                navigateToPreviousFragment()
            }
        })
    }

}