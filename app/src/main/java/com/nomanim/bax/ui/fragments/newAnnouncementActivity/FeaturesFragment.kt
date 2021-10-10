package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentFeaturesBinding
import com.nomanim.bax.ui.other.ktx.showDialogOfCloseActivity
import com.nomanim.bax.ui.other.ktx.showFeaturesBottomSheet

class FeaturesFragment : Fragment() {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!
    private val storageList = ArrayList<String>()
    private val ramList = ArrayList<String>()
    private val colorList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFeaturesBinding.inflate(inflater,container,false)

        binding.featuresToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.featuresNextToolbarButton.setOnClickListener { navigateToPriceFragment(it) }
        binding.featuresNextButton.setOnClickListener { navigateToPriceFragment(it) }
        binding.featuresCancelButton.setOnClickListener { showDialogOfCloseActivity() }

        storageList.add("8 GB")
        storageList.add("16 GB")
        storageList.add("32 GB")

        ramList.add("2 GB")
        ramList.add("4 GB")
        ramList.add("8 GB")

        colorList.add("Red")
        colorList.add("Blue")
        colorList.add("Yellow")

        binding.chooseStorageCardView.setOnClickListener {

            showFeaturesBottomSheet(storageList,binding.storageTextView,R.string.choose_phone_storage) }

        binding.chooseRamCardView.setOnClickListener {

            showFeaturesBottomSheet(ramList,binding.ramTextView,R.string.choose_phone_ram) }

        binding.chooseColorCardView.setOnClickListener {

            showFeaturesBottomSheet(colorList,binding.colorTextView,R.string.choose_phone_color) }

        return binding.root
    }

    private fun navigateToPriceFragment(view: View) {

        val storageCapacity = binding.storageTextView.text.toString()
        val ramCapacity = binding.ramTextView.text.toString()
        val color = binding.colorTextView.text.toString()
        val radioButtonId: Int = binding.phoneStatusRadioGroup.checkedRadioButtonId
        var status = ""
        if (radioButtonId != -1) { status = binding.phoneStatusRadioGroup.findViewById<RadioButton>(radioButtonId).text.toString() }

        if (storageCapacity == "" || ramCapacity == "" || color == "" || status == "") {

            Snackbar.make(view,resources.getString(R.string.fill_in_all),Snackbar.LENGTH_SHORT).show()

        }else {

            val sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.putString("storageCapacity",storageCapacity)
            editor?.putString("ramCapacity",ramCapacity)
            editor?.putString("color",color)
            editor?.apply()

            findNavController().navigate(R.id.action_featuresFragment_to_priceFragment)
        }
    }

}