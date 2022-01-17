package com.nomanim.bunual.ui.fragments.newadsactivity

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
import com.nomanim.bunual.extensions.showDialogOfCloseActivity

class DescriptionFragment : Fragment() {

    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DescriptionFragmentArgs>()
    private var sharedPref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = activity?.getSharedPreferences("newAdsActivity", Context.MODE_PRIVATE)

        pressBackButton()
        binding.descriptionEditText.setText(sharedPref?.getString("description", ""))
        binding.descriptionToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.descriptionNextButton.setOnClickListener {
            checkEditTextAndNavigate(it)
        }
        binding.descriptionNextToolbarButton.setOnClickListener {
            checkEditTextAndNavigate(it)
        }
        binding.descriptionCancelButton.setOnClickListener {
            showDialogOfCloseActivity()
        }
    }

    private fun checkEditTextAndNavigate(view: View) {
        if (TextUtils.isEmpty(binding.descriptionEditText.text.toString())) {
            Snackbar.make(view, resources.getString(R.string.fill_in_all), Snackbar.LENGTH_SHORT)
                .show()
        } else {
            val editor = sharedPref?.edit()
            val descriptionText = binding.descriptionEditText.text.toString()
            editor?.putString("description", descriptionText)
            editor?.apply()

            val announcement = args.announcement.copy(description = descriptionText)
            val action = DescriptionFragmentDirections.actionDescriptionFragmentToFeaturesFragment(
                announcement
            )
            findNavController().navigate(action)
        }
    }


    private fun pressBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val editor = sharedPref?.edit()
                    editor?.putString("description", binding.descriptionEditText.text.toString())
                    editor?.apply()

                    findNavController().navigate(
                        DescriptionFragmentDirections.actionDescriptionFragmentToModelsFragment(
                            args.announcement
                        )
                    )
                }
            })
    }
}