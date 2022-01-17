package com.nomanim.bunual.ui.fragments.newadsactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentFeaturesBinding
import com.nomanim.bunual.databinding.LayoutBottomSheetColorsBinding
import com.nomanim.bunual.models.ModelColors
import com.nomanim.bunual.adapters.ColorsAdapter
import com.nomanim.bunual.extensions.showDialogOfCloseActivity
import com.nomanim.bunual.extensions.showCustomBottomSheet

class FeaturesFragment : Fragment(), ColorsAdapter.Listener {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<FeaturesFragmentArgs>()
    private lateinit var bottomSheetBinding: LayoutBottomSheetColorsBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val storageList = ArrayList<String>()
    private val ramList = ArrayList<String>()
    private val colorList = ArrayList<ModelColors>()
    private var statusOfPhone: String = ""
    private var stringOfStorage: String = ""
    private var stringOfRam: String = ""
    private var stringOfColor: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeaturesBinding.inflate(inflater, container, false)
        bottomSheetBinding = LayoutBottomSheetColorsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pressBackButton()
        getStringsFromRes()
        initRadioButton()

        binding.featuresToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.featuresNextToolbarButton.setOnClickListener {
            navigateToNextFragment(it)
        }
        binding.featuresNextButton.setOnClickListener {
            navigateToNextFragment(it)
        }
        binding.featuresCancelButton.setOnClickListener {
            showDialogOfCloseActivity()
        }
        binding.chooseStorageCardView.setOnClickListener {
            showCustomBottomSheet(
                storageList,
                binding.storageTextView,
                getString(R.string.choose_phone_storage),
                false
            )
        }
        binding.chooseRamCardView.setOnClickListener {
            showCustomBottomSheet(
                ramList,
                binding.ramTextView,
                getString(R.string.choose_phone_ram),
                false
            )
        }
        binding.chooseColorCardView.setOnClickListener {
            setColorsBottomSheet()
        }
    }

    private fun getStringsFromRes() {
        for (capacity in resources.getStringArray(R.array.storageCapacities)) {
            storageList.add(capacity)
        }
        for (capacity in resources.getStringArray(R.array.ramCapacities)) {
            ramList.add(capacity)
        }
        for (colorName in resources.getStringArray(R.array.colorNames)) {
            val modelColor = ModelColors(colorName, R.color.background_color_gray)
            colorList.add(modelColor)
        }
    }

    private fun initRadioButton() {
        binding.phoneStatusRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.newPhone -> {
                    statusOfPhone = getString(R.string.new_)
                }
                R.id.asSparePartPhone -> {
                    statusOfPhone = getString(R.string.as_spare_part)
                }
                R.id.usedPhone -> {
                    statusOfPhone = getString(R.string.used)
                }
            }
        }
    }

    private fun setColorsBottomSheet() {
        val bottomSheetView = bottomSheetBinding.root
        if (bottomSheetView.parent != null) {
            (bottomSheetView.parent as ViewGroup).removeAllViews()
        }
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        setColorsRecyclerView()
    }

    private fun setColorsRecyclerView() {
        val recyclerView = bottomSheetBinding.placesRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ColorsAdapter(colorList, this@FeaturesFragment)
        recyclerView.adapter = adapter
    }

    private fun navigateToNextFragment(view: View) {
        val storage = binding.storageTextView.text.toString()
        val ram = binding.ramTextView.text.toString()
        val color = binding.colorTextView.text.toString()
        if (storage != stringOfStorage &&
            ram != stringOfRam &&
            color != stringOfColor &&
            statusOfPhone != ""
        ) {
            val phone = args.announcement.phone.copy(
                storage = storage,
                ram = ram,
                color = color,
                currentStatus = statusOfPhone
            )
            val announcement = args.announcement.copy(phone = phone)
            val action = FeaturesFragmentDirections.actionFeaturesFragmentToPriceFragment(
                announcement
            )
            findNavController().navigate(action)
        } else {
            Snackbar.make(view, resources.getString(R.string.fill_in_all), Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun setOnColorClickListener(buttonFinishText: String) {
        binding.colorTextView.text = buttonFinishText
        bottomSheetDialog.dismiss()
    }

    private fun pressBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(
                        FeaturesFragmentDirections.actionFeaturesFragmentToDescriptionFragment(
                            args.announcement
                        )
                    )
                }
            })
    }
}