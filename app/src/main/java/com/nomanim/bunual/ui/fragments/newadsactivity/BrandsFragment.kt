package com.nomanim.bunual.ui.fragments.newadsactivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.PhoneBrandsAdapter
import com.nomanim.bunual.databinding.FragmentBrandsBinding
import com.nomanim.bunual.api.entity.BrandsResponse
import com.nomanim.bunual.api.entity.RegionsResponse
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.ui.activities.MainActivity
import com.nomanim.bunual.base.BaseCoroutineScope
import com.nomanim.bunual.extensions.showDialogOfCloseActivity
import com.nomanim.bunual.base.clearTextWhenClickClear
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.models.ModelPhone
import com.nomanim.bunual.models.ModelUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class BrandsFragment : BaseCoroutineScope(), PhoneBrandsAdapter.Listener {

    private var _binding: FragmentBrandsBinding? = null
    private val binding get() = _binding!!
    private var phoneBrands = ArrayList<BrandsResponse.Body>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrandsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed()
        binding.brandsToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.closeActivityInBrandsFragment.setOnClickListener {
            showDialogOfCloseActivity()
        }
        binding.searchPhoneBrands.clearTextWhenClickClear()

        getBrandNamesFromRoom()
    }

    private fun getBrandNamesFromRoom() {
        launch {
            val database = RoomDB(requireContext()).getDataFromRoom()
            phoneBrands = database.getBrandNamesFromDb() as ArrayList<BrandsResponse.Body>
            setBrandsRecyclerView(phoneBrands)
            binding.brandsProgressBar.visibility = View.INVISIBLE
            searchInsidePhoneModels()
        }
    }

    private fun searchInsidePhoneModels() {
        binding.searchPhoneBrands.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(text: Editable?) {
                lifecycleScope.launch {
                    delay(1000)
                    val listAfterSearch = phoneBrands.filter { list ->
                        (list.brandName.lowercase().contains(text.toString().lowercase()))
                    } as ArrayList<BrandsResponse.Body>
                    setBrandsRecyclerView(listAfterSearch)
                    binding.brandsProgressBar.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun setBrandsRecyclerView(brandsList: ArrayList<BrandsResponse.Body>) {
        val brv = binding.brandsRecyclerView
        brv.isNestedScrollingEnabled = false
        brv.layoutManager = LinearLayoutManager(requireContext())
        brv.setHasFixedSize(true)
        val adapter = PhoneBrandsAdapter(requireContext(), brandsList, this@BrandsFragment)
        brv.adapter = adapter
    }

    override fun onCardViewClickListener(brandId: String, brandName: String) {
        try {
            val sharedPref = activity?.getSharedPreferences("newAdsActivity", Context.MODE_PRIVATE)
            sharedPref?.edit()?.putString("phoneBrandId", brandId)?.apply()

            val places = RegionsResponse("", "0")
            val user = ModelUser("", "", places)
            val phone = ModelPhone(
                brandName,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                false
            )
            val announcement = ModelAnnouncement(
                "",
                "",
                ArrayList(),
                "",
                "0",
                Timestamp.now(),
                phone,
                user
            )
            val action = BrandsFragmentDirections.actionBrandsFragmentToModelsFragment(announcement)
            findNavController().navigate(action)

        } catch (e: Exception) {
        }
    }

    private fun onBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(activity, MainActivity::class.java)
                    activity?.finish()
                    activity?.startActivity(intent)
                    activity?.overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}