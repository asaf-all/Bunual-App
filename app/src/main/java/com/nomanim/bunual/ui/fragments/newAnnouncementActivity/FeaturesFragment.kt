package com.nomanim.bunual.ui.fragments.newAnnouncementActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentFeaturesBinding
import com.nomanim.bunual.databinding.LayoutBottomSheetColorsBinding
import com.nomanim.bunual.models.ModelColors
import com.nomanim.bunual.adapters.ColorsAdapter
import com.nomanim.bunual.ui.other.ktx.showDialogOfCloseActivity
import com.nomanim.bunual.ui.other.ktx.showFeaturesBottomSheet

class FeaturesFragment : Fragment(), ColorsAdapter.Listener {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!
    private var sharedPref: SharedPreferences? = null
    private lateinit var bottomSheetBinding: LayoutBottomSheetColorsBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val storageList = ArrayList<String>()
    private val ramList = ArrayList<String>()
    private val colorList = ArrayList<ModelColors>()
    private var statusOfPhoneOfAds: String = ""
    private var stringOfStorage: String = ""
    private var stringOfRam: String = ""
    private var stringOfColor: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFeaturesBinding.inflate(inflater,container,false)
        bottomSheetBinding = LayoutBottomSheetColorsBinding.inflate(inflater)
        sharedPref = activity?.getSharedPreferences("sharedPrefInNewAdsActivity",Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAndSetAllDataIfHasAccess()
        checkRadioGroupOfPhoneStatus()
        pressBackButton()
        getStorageCapacities()
        getRamCapacities()
        getColorNamesAndCodes()

        binding.featuresToolbar.setNavigationOnClickListener { navigateToPreviousFragment() }
        binding.featuresNextToolbarButton.setOnClickListener { navigateToNextFragment(it) }
        binding.featuresNextButton.setOnClickListener { navigateToNextFragment(it) }
        binding.featuresCancelButton.setOnClickListener { showDialogOfCloseActivity() }

        binding.chooseStorageCardView.setOnClickListener {
            showFeaturesBottomSheet(storageList,binding.storageTextView,getString(R.string.choose_phone_storage),false) }

        binding.chooseRamCardView.setOnClickListener {
            showFeaturesBottomSheet(ramList,binding.ramTextView,getString(R.string.choose_phone_ram),false) }

        binding.chooseColorCardView.setOnClickListener { setColorsBottomSheet() }
    }

    private fun getStorageCapacities() {

        for (capacity in resources.getStringArray(R.array.storageCapacities) ) {
            storageList.add(capacity)
        }
    }

    private fun getRamCapacities() {

        for (capacity in resources.getStringArray(R.array.ramCapacities) ) {
            ramList.add(capacity)
        }
    }

    private fun getColorNamesAndCodes() {

        for (colorName in resources.getStringArray(R.array.colorNames) ) {

            val modelColor = ModelColors(colorName,R.color.background_color_gray)
            colorList.add(modelColor)
        }
    }

    private fun setColorsBottomSheet() {

        val bottomSheetView = bottomSheetBinding.root
        if (bottomSheetView.parent != null) {

            (bottomSheetView.parent as ViewGroup).removeAllViews()
        }
        context?.let {

            bottomSheetDialog = BottomSheetDialog(it)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
        setColorsRecyclerView()
    }

    private fun setColorsRecyclerView() {

        val recyclerView = bottomSheetBinding.placesRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ColorsAdapter(colorList,this@FeaturesFragment)
        recyclerView.adapter = adapter
    }

    private fun getAndSetAllDataIfHasAccess() {

        stringOfStorage = getString(R.string.choose_phone_storage)
        stringOfRam = getString(R.string.choose_phone_ram)
        stringOfColor = getString(R.string.choose_phone_color)

        val lastStorageCapacity = sharedPref?.getString("storageCapacity",stringOfStorage)
        val lastRamCapacity = sharedPref?.getString("ramCapacity",stringOfRam)
        val lastColorName = sharedPref?.getString("color",stringOfColor)

        binding.storageTextView.text = lastStorageCapacity
        binding.ramTextView.text = lastRamCapacity
        binding.colorTextView.text = lastColorName
    }

    private fun navigateToNextFragment(view: View) {

        val storageCapacity = binding.storageTextView.text.toString()
        val ramCapacity = binding.ramTextView.text.toString()
        val colorName = binding.colorTextView.text.toString()

        if (storageCapacity != stringOfStorage && ramCapacity != stringOfRam
            && colorName != stringOfColor && statusOfPhoneOfAds != "") {

             addAllFeaturesAtSharedPreferences(storageCapacity, ramCapacity, colorName)
             findNavController().navigate(R.id.action_featuresFragment_to_priceFragment)

        }else  {

            Snackbar.make(view,resources.getString(R.string.fill_in_all),Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun addAllFeaturesAtSharedPreferences(storageCapacity: String, ramCapacity: String, colorName: String) {

        val editor = sharedPref?.edit()
        editor?.putString("storageCapacity",storageCapacity)
        editor?.putString("ramCapacity",ramCapacity)
        editor?.putString("color",colorName)
        editor?.putString("status",statusOfPhoneOfAds)
        editor?.apply()
    }

    private fun checkRadioGroupOfPhoneStatus() {

        binding.phoneStatusRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.newPhone -> { statusOfPhoneOfAds = getString(R.string.new_) }
                R.id.asSparePartPhone -> { statusOfPhoneOfAds = getString(R.string.as_spare_part) }
                R.id.usedPhone -> { statusOfPhoneOfAds = getString(R.string.used) }
            }
        }
    }

    private fun navigateToPreviousFragment() {

        val storageCapacity = binding.storageTextView.text.toString()
        val ramCapacity = binding.ramTextView.text.toString()
        val colorName = binding.colorTextView.text.toString()

        addAllFeaturesAtSharedPreferences(storageCapacity, ramCapacity, colorName)

        findNavController().navigate(R.id.action_featuresFragment_to_descriptionFragment)
    }

    private fun pressBackButton() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                navigateToPreviousFragment()
            }
        })
    }

    override fun setOnColorClickListener(buttonFinishText: String) {

        binding.colorTextView.text = buttonFinishText
        bottomSheetDialog.dismiss()
    }
}