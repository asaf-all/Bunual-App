package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.content.Context
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
import com.nomanim.bax.ui.other.ktx.showDialogOfCloseActivity

class PriceFragment : Fragment() {

    private var _binding: FragmentPriceBinding? = null
    private val binding get() = _binding!!
    private var radioButtonActive = false
    private var bundle: Bundle? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentPriceBinding.inflate(inflater,container,false)

        binding.byAgreementSwitch.setOnClickListener { checkSwitchStatus() }
        binding.priceToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.priceNextToolbarButton.setOnClickListener { navigateToNextFragment(it) }
        binding.priceNextButton.setOnClickListener { navigateToNextFragment(it) }
        binding.priceCancelButton.setOnClickListener { showDialogOfCloseActivity() }

        return binding.root
    }

    private fun checkSwitchStatus() {

        if (radioButtonActive) {

            radioButtonActive = false
            binding.price.visibility = View.VISIBLE

        }else {

            radioButtonActive = true
            binding.price.visibility = View.INVISIBLE
        }
    }

    private fun navigateToNextFragment(view: View) {

        val sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        if (binding.priceEditText.visibility == View.VISIBLE) {

            editor?.putString("price",binding.priceEditText.text.toString() + " AZN")
            checkPriceEditTextAndNavigate(view)

        }else {

            editor?.putInt("priceWithAgreement",R.string.by_agreement)
        }

        editor?.apply()
    }

    private fun checkPriceEditTextAndNavigate(view: View) {

        if (TextUtils.isEmpty(binding.priceEditText.text.toString())) {

            Snackbar.make(view,getString(R.string.fill_in_all),Snackbar.LENGTH_SHORT).show()

        }else {

            findNavController().navigate(R.id.action_priceFragment_to_userFragment,bundle)
        }
    }

}