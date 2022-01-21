package com.nomanim.bunual.ui.fragments.newadsactivity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.PhoneModelsAdapter
import com.nomanim.bunual.databinding.FragmentModelsBinding
import com.nomanim.bunual.api.entity.ModelsResponse
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.base.BaseCoroutineScope
import com.nomanim.bunual.extensions.showDialogOfCloseActivity
import com.nomanim.bunual.base.clearTextWhenClickClear
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ModelsFragment : BaseCoroutineScope(), PhoneModelsAdapter.Listener {

    private var _binding: FragmentModelsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ModelsFragmentArgs>()

    private var filteredList = ArrayList<ModelsResponse.Body>()
    private val limitedAndFilteredList = ArrayList<ModelsResponse.Body>()
    private val limitedListAfterSearch = ArrayList<ModelsResponse.Body>()
    private var lastLoudIndex: Int = 30
    private var numberOfModelName = 30
    private var numberOfSearchedModelName = 20
    private var remainingFilteredListSize: Int = 0
    private var remainingListAfterSearchSize: Int = 0
    private lateinit var recyclerAdapter: PhoneModelsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pressBackButton()
        getModelsNamesFromRoom()
        binding.modelsToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.closeActivityInModelsFragment.setOnClickListener {
            showDialogOfCloseActivity()
        }
        binding.searchPhoneModels.clearTextWhenClickClear()
    }

    private fun getModelsNamesFromRoom() {
        launch {
            val database = RoomDB(requireContext()).getDataFromRoom()
            val phoneModelNames = database.getModelNamesFromDb() as ArrayList<ModelsResponse.Body>
            filterPhoneModelNamesByBrandId(phoneModelNames)
        }
    }

    private fun filterPhoneModelNamesByBrandId(modelsList: ArrayList<ModelsResponse.Body>) {
        if (modelsList.isNotEmpty()) {
            val sharedPref = activity?.getSharedPreferences("newAdsActivity", Context.MODE_PRIVATE)
            val phoneBrandId = sharedPref?.getString("phoneBrandId", null)
            filteredList =
                modelsList.filter { (it.brandId) == phoneBrandId } as ArrayList<ModelsResponse.Body>
            if (filteredList.size < numberOfModelName) {
                numberOfModelName = filteredList.size
                binding.moreModelsProgressBar.visibility = View.INVISIBLE
            } else {
                remainingFilteredListSize = filteredList.size
                addMoreModelNamesAtRecyclerView(remainingFilteredListSize)
                binding.moreModelsProgressBar.visibility = View.VISIBLE
            }
            for (index in 0 until numberOfModelName) {
                limitedAndFilteredList.add(filteredList[index])
            }
            setModelsRecyclerView(limitedAndFilteredList)
            searchInsidePhoneModels()
        }
        binding.modelsProgressBar.visibility = View.INVISIBLE
    }

    private fun setModelsRecyclerView(modelsList: ArrayList<ModelsResponse.Body>) {
        val recyclerView = binding.modelsRecyclerView
        recyclerView.visibility = View.VISIBLE
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerAdapter = PhoneModelsAdapter(requireContext(), modelsList, this@ModelsFragment)
        recyclerView.adapter = recyclerAdapter
    }

    private fun addMoreModelNamesAtRecyclerView(_remainingListSize: Int) {
        var remainingListSize = _remainingListSize
        val scrollView = binding.modelsNestedScollView
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.getChildAt(0).bottom <= (scrollView.height + scrollView.scrollY)) {
                remainingListSize -= numberOfModelName
                if (remainingListSize >= 0) {
                    if (remainingListSize < numberOfModelName) {
                        numberOfModelName = remainingListSize
                        binding.moreModelsProgressBar.visibility = View.INVISIBLE
                    }
                    for (index in lastLoudIndex until lastLoudIndex + numberOfModelName) {
                        limitedAndFilteredList.add(filteredList[index])
                    }
                    lastLoudIndex += numberOfModelName
                    recyclerAdapter.notifyDataSetChanged()
                } else {
                    binding.moreModelsProgressBar.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun searchInsidePhoneModels() {
        binding.searchPhoneModels.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(text: Editable?) {
                lifecycleScope.launch {
                    delay(1000)
                    val listAfterSearch = filteredList.filter { list ->
                        (list.modelName.lowercase().contains(text.toString().lowercase()))
                    } as ArrayList<ModelsResponse.Body>
                    if (listAfterSearch.size < numberOfSearchedModelName) {
                        numberOfSearchedModelName = listAfterSearch.size
                        binding.moreModelsProgressBar.visibility = View.INVISIBLE
                    } else {
                        remainingListAfterSearchSize = listAfterSearch.size
                        addMoreModelNamesAtRecyclerView(remainingListAfterSearchSize)
                        binding.moreModelsProgressBar.visibility = View.VISIBLE
                    }
                    limitedListAfterSearch.clear()
                    for (index in 0 until numberOfSearchedModelName) {
                        limitedListAfterSearch.add(listAfterSearch[index])
                    }
                    setModelsRecyclerView(limitedListAfterSearch)
                    binding.modelsProgressBar.visibility = View.INVISIBLE
                }
            }
        })
    }

    override fun onCardViewClickListener(modelName: String) {
        try {
            val phone = args.announcement.phone.copy(model = modelName)
            val announcement = args.announcement.copy(phone = phone)

            val action =
                ModelsFragmentDirections.actionModelsFragmentToDescriptionFragment(announcement)
            findNavController().navigate(action)
        } catch (e: Exception) {
        }
    }

    private fun pressBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_modelsFragment_to_brandsFragment)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}