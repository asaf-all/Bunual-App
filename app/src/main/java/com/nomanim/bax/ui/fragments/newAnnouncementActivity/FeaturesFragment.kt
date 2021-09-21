package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nomanim.bax.R
import com.nomanim.bax.adapters.FeaturesSheetRecyclerView
import com.nomanim.bax.databinding.FragmentFeaturesBinding

class FeaturesFragment : Fragment(),FeaturesSheetRecyclerView.Listener {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!
    private  lateinit var bottomSheetView: View
    val list = ArrayList<String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFeaturesBinding.inflate(inflater,container,false)

        bottomSheetView = layoutInflater.inflate(R.layout.layout_bottom_sheet_phone_storage,container,false)

        list.add("8 GB")
        list.add("16 GB")
        list.add("32 GB")
        list.add("64 GB")
        list.add("128 GB")

        binding.featuresToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.featuresNextToolbarButton.setOnClickListener { activity?.onBackPressed() }
        binding.featuresNextButton.setOnClickListener { activity?.onBackPressed() }
        binding.chooseStorageCardView.setOnClickListener { setBottomSheet() }

        return binding.root
    }

    private fun setBottomSheet() {

        if (bottomSheetView.parent != null) {

            (bottomSheetView.parent as ViewGroup).removeAllViews()
        }
        context?.let {

            val bottomSheet = BottomSheetDialog(it)
            bottomSheet.setContentView(bottomSheetView)
            bottomSheet.show()
            setStorageCapacityRecyclerView(list)
        }
    }

    private fun setStorageCapacityRecyclerView(storages: ArrayList<String>) {

        val recyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.phoneStorageRecyclerView)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        context?.let {

            recyclerView.layoutManager = LinearLayoutManager(it)
            val adapter = FeaturesSheetRecyclerView(storages,this@FeaturesFragment)
            recyclerView.adapter = adapter
        }
    }

}