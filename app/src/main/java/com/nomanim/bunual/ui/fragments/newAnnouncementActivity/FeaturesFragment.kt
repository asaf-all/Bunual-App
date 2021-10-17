package com.nomanim.bunual.ui.fragments.newAnnouncementActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentFeaturesBinding
import com.nomanim.bunual.ui.other.ktx.showDialogOfCloseActivity
import com.nomanim.bunual.ui.other.ktx.showFeaturesBottomSheet

class FeaturesFragment : Fragment() {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!
    private var sharedPref: SharedPreferences? = null
    private val storageList = ArrayList<String>()
    private val ramList = ArrayList<String>()
    private val colorList = ArrayList<String>()
    private var stringOfStorage: String = ""
    private var stringOfRam: String = ""
    private var stringOfColor: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFeaturesBinding.inflate(inflater,container,false)
        sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)

        pressBackButton()
        getStrings()

        if (sharedPref?.getBoolean("getDataFromSharedPref",false) == true) {

            getAllDataIfHasAccess()
        }

        binding.featuresToolbar.setNavigationOnClickListener { navigateToPreviousFragment() }
        binding.featuresNextToolbarButton.setOnClickListener { navigateToNextFragment(it) }
        binding.featuresNextButton.setOnClickListener { navigateToNextFragment(it) }
        binding.featuresCancelButton.setOnClickListener { showDialogOfCloseActivity() }

        binding.chooseStorageCardView.setOnClickListener {

            showFeaturesBottomSheet(storageList,binding.storageTextView,R.string.choose_phone_storage) }

        binding.chooseRamCardView.setOnClickListener {

            showFeaturesBottomSheet(ramList,binding.ramTextView,R.string.choose_phone_ram) }

        binding.chooseColorCardView.setOnClickListener {

            showFeaturesBottomSheet(colorList,binding.colorTextView,R.string.choose_phone_color) }

        return binding.root
    }

    private fun getStrings() {

        val storageIdentifier = resources.getIdentifier("choose_phone_storage","string",activity?.packageName)
        val ramIdentifier = resources.getIdentifier("choose_phone_ram","string",activity?.packageName)
        val colorIdentifier = resources.getIdentifier("choose_phone_color","string",activity?.packageName)

        stringOfStorage = getString(storageIdentifier)
        stringOfRam = getString(ramIdentifier)
        stringOfColor = getString(colorIdentifier)
    }

    private fun getAllDataIfHasAccess() {

        val storageCapacity = sharedPref?.getString("storageCapacity",stringOfStorage)
        val ramCapacity = sharedPref?.getString("ramCapacity",stringOfRam)
        val color = sharedPref?.getString("color",stringOfColor)
        val radioGroupSelectedItemId = sharedPref?.getInt("radioGroupSelectedItemId",0)?.plus(1)

        binding.storageTextView.text = storageCapacity
        binding.ramTextView.text = ramCapacity
        binding.colorTextView.text = color
        binding.phoneStatusRadioGroup.check(radioGroupSelectedItemId!!)
    }

    private fun navigateToNextFragment(view: View) {

        val storageCapacity = binding.storageTextView.text.toString()
        val ramCapacity = binding.ramTextView.text.toString()
        val color = binding.colorTextView.text.toString()
        val radioButtonId = binding.phoneStatusRadioGroup.checkedRadioButtonId
        var status = ""
        if (radioButtonId != -1) { status = binding.phoneStatusRadioGroup.findViewById<RadioButton>(radioButtonId).text.toString() }

        if (storageCapacity == stringOfStorage || ramCapacity == stringOfRam || color == stringOfColor || status == "") {

            Snackbar.make(view,resources.getString(R.string.fill_in_all),Snackbar.LENGTH_SHORT).show()

        }else {

            val editor = sharedPref?.edit()
            editor?.putString("storageCapacity",storageCapacity)
            editor?.putString("ramCapacity",ramCapacity)
            editor?.putString("color",color)
            editor?.putString("status",status)
            editor?.putInt("radioGroupSelectedItemId",radioButtonId)
            editor?.putBoolean("getDataFromSharedPref",true)
            editor?.apply()

            findNavController().navigate(R.id.action_featuresFragment_to_priceFragment)
        }
    }

    private fun navigateToPreviousFragment() {

        findNavController().navigate(R.id.action_featuresFragment_to_descriptionFragment)
    }

    private fun pressBackButton() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                navigateToPreviousFragment()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        val editor = sharedPref?.edit()
        editor?.putBoolean("getDataFromSharedPref",false)
        editor?.apply()
    }

}