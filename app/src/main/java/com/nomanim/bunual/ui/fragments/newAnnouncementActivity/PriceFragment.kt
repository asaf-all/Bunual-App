package com.nomanim.bunual.ui.fragments.newAnnouncementActivity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
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
    private var withAgreement: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentPriceBinding.inflate(inflater,container,false)

        pressBackButton()

        binding.byAgreementSwitch.isChecked = false
        binding.byAgreementSwitch.setOnCheckedChangeListener { view, isChecked -> checkSwitchButtonStatus(isChecked) }
        binding.priceToolbar.setNavigationOnClickListener { navigateToPreviousFragment() }
        binding.priceNextToolbarButton.setOnClickListener { navigateToNextFragment(it) }
        binding.priceNextButton.setOnClickListener { navigateToNextFragment(it) }
        binding.priceCancelButton.setOnClickListener { showDialogOfCloseActivity() }

        return binding.root
    }

    private fun checkSwitchButtonStatus(switchButtonStatus: Boolean) {

        if (!switchButtonStatus) {

            withAgreement = false
            binding.price.visibility = View.VISIBLE

        }else {

            withAgreement = true
            binding.price.visibility = View.INVISIBLE
        }
    }

    private fun navigateToNextFragment(view: View) {

        val sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        if (!withAgreement) {

            editor?.putString("price",binding.priceEditText.text.toString() + " AZN")
            checkPriceEditTextAndNavigate(view)

        }else {

            val identifier = resources.getIdentifier("by_agreement","string",activity?.packageName)
            editor?.putString("price",getString(identifier))
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