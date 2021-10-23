package com.nomanim.bunual.ui.fragments.newAnnouncementActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentDescriptionBinding
import com.nomanim.bunual.ui.other.ktx.showDialogOfCloseActivity

class DescriptionFragment : Fragment() {

    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DescriptionFragmentArgs>()
    private var sharedPref: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDescriptionBinding.inflate(inflater,container,false)
        sharedPref = activity?.getSharedPreferences("sharedPrefInNewAdsActivity",Context.MODE_PRIVATE)

        pressBackButton()

        if (args.fromFeatures) {

            binding.descriptionEditText.setText(sharedPref?.getString("description",""))

        }else { binding.descriptionEditText.setText("") }

        binding.descriptionToolbar.setNavigationOnClickListener { navigateToPreviousFragment() }
        binding.descriptionNextButton.setOnClickListener { checkEditTextAndNavigate(it) }
        binding.descriptionNextToolbarButton.setOnClickListener { checkEditTextAndNavigate(it) }
        binding.descriptionCancelButton.setOnClickListener { showDialogOfCloseActivity() }

        return binding.root
    }

    private fun checkEditTextAndNavigate(view: View) {

        if (TextUtils.isEmpty(binding.descriptionEditText.text.toString())) {

            Snackbar.make(view,resources.getString(R.string.fill_in_all), Snackbar.LENGTH_SHORT).show()

        }else { navigateToNextFragment() }

    }

    private fun navigateToPreviousFragment() {

        val actionWithArgument = DescriptionFragmentDirections.actionDescriptionFragmentToModelsFragment(true)
        findNavController().navigate(actionWithArgument)
    }

    private fun pressBackButton() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                navigateToPreviousFragment()
            }
        })
    }

    private fun navigateToNextFragment() {

        val editor = sharedPref?.edit()
        editor?.putString("description",binding.descriptionEditText.text.toString())
        editor?.apply()

        findNavController().navigate(R.id.action_descriptionFragment_to_featuresFragment)
    }

}