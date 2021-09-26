package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bax.R
import com.nomanim.bax.adapters.ColorSheetAdapter
import com.nomanim.bax.adapters.RamSheetAdapter
import com.nomanim.bax.adapters.StorageSheetAdapter
import com.nomanim.bax.databinding.FragmentFeaturesBinding
import com.nomanim.bax.databinding.LayoutBottomSheetPhoneColorBinding
import com.nomanim.bax.databinding.LayoutBottomSheetPhoneRamBinding
import com.nomanim.bax.databinding.LayoutBottomSheetPhoneStorageBinding

class FeaturesFragment : Fragment(),StorageSheetAdapter.Listener, RamSheetAdapter.Listener, ColorSheetAdapter.Listener {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!
    private lateinit var storageBottomSheetBinding: LayoutBottomSheetPhoneStorageBinding
    private lateinit var ramBottomSheetBinding: LayoutBottomSheetPhoneRamBinding
    private lateinit var colorBottomSheetBinding: LayoutBottomSheetPhoneColorBinding
    private val storageList = ArrayList<String>()
    private val ramList = ArrayList<String>()
    private val colorList = ArrayList<String>()
    private lateinit var storageBottomSheet: BottomSheetDialog
    private lateinit var ramBottomSheet: BottomSheetDialog
    private lateinit var colorBottomSheet: BottomSheetDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFeaturesBinding.inflate(inflater,container,false)

        storageBottomSheetBinding = LayoutBottomSheetPhoneStorageBinding.inflate(inflater)
        ramBottomSheetBinding = LayoutBottomSheetPhoneRamBinding.inflate(inflater)
        colorBottomSheetBinding = LayoutBottomSheetPhoneColorBinding.inflate(inflater)

        context?.let { context ->

            storageBottomSheet = BottomSheetDialog(context)
            ramBottomSheet = BottomSheetDialog(context)
            colorBottomSheet = BottomSheetDialog(context)
        }

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

            setBottomSheet(storageBottomSheetBinding.root,storageBottomSheet)
            setStorageRecyclerView()
        }
        binding.chooseRamCardView.setOnClickListener {

            setBottomSheet(ramBottomSheetBinding.root,ramBottomSheet)
            setRamRecyclerView()
        }
        binding.chooseColorCardView.setOnClickListener {

            setBottomSheet(colorBottomSheetBinding.root,colorBottomSheet)
            setColorRecyclerView()
        }

        onBackPressed()
        binding.featuresToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.featuresNextToolbarButton.setOnClickListener { navigateToPriceFragment(it) }
        binding.featuresNextButton.setOnClickListener { navigateToPriceFragment(it) }

        return binding.root
    }

    private fun setBottomSheet(bottomSheetView:View,bottomSheetDialog: BottomSheetDialog) {

        if (bottomSheetView.parent != null) {

            (bottomSheetView.parent as ViewGroup).removeAllViews()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun setStorageRecyclerView() {

        val recyclerView = storageBottomSheetBinding.storageRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        context?.let {

            recyclerView.layoutManager = LinearLayoutManager(it)
            val adapter = StorageSheetAdapter(storageList,this@FeaturesFragment)
            recyclerView.adapter = adapter
        }
    }

    private fun setRamRecyclerView() {

        val recyclerView = ramBottomSheetBinding.ramRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        context?.let {

            recyclerView.layoutManager = LinearLayoutManager(it)
            val adapter = RamSheetAdapter(ramList,this@FeaturesFragment)
            recyclerView.adapter = adapter
        }
    }

    private fun setColorRecyclerView() {

        val recyclerView = colorBottomSheetBinding.colorRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        context?.let {

            recyclerView.layoutManager = LinearLayoutManager(it)
            val adapter = ColorSheetAdapter(colorList,this@FeaturesFragment)
            recyclerView.adapter = adapter
        }
    }

    override fun setOnStorageClickListener(buttonFinishText: String) {

        binding.storageTextView.text = buttonFinishText
        storageBottomSheet.dismiss()
    }

    override fun setOnRamClickListener(buttonFinishText: String) {

        binding.ramTextView.text = buttonFinishText
        ramBottomSheet.dismiss()
    }

    override fun setOnColorClickListener(buttonFinishText: String) {

        binding.colorTextView.text = buttonFinishText
        colorBottomSheet.dismiss()
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

            val bundle = arguments?.getBundle("descriptionBundle")
            bundle?.putString("storageCapacity",storageCapacity)
            bundle?.putString("ramCapacity",ramCapacity)
            bundle?.putString("color",color)
            bundle?.putBundle("featuresBundle",bundle)

            findNavController().navigate(R.id.action_featuresFragment_to_priceFragment,bundle)
        }
    }

    private fun onBackPressed() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {


            }
        })
    }

}