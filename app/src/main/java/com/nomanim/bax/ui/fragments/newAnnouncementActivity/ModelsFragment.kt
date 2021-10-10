package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nomanim.bax.R
import com.nomanim.bax.adapters.PhoneModelsAdapter
import com.nomanim.bax.databinding.FragmentModelsBinding
import com.nomanim.bax.retrofit.models.ModelPhoneModels
import com.nomanim.bax.room.database.RoomDB
import com.nomanim.bax.ui.other.BaseCoroutineScope
import com.nomanim.bax.ui.other.clearTextWhenClickClear
import com.nomanim.bax.ui.other.ktx.showDialogOfCloseActivity
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ModelsFragment : BaseCoroutineScope(),PhoneModelsAdapter.Listener {

    private var _binding: FragmentModelsBinding? = null
    private val binding get() = _binding!!
    private var sharedPref: SharedPreferences? = null
    private var phoneBrandId: String? = null
    private var filteredList = ArrayList<ModelPhoneModels>()
    private val limitedAndFilteredList = ArrayList<ModelPhoneModels>()
    private val limitedListAfterSearch = ArrayList<ModelPhoneModels>()
    private var lastLoudIndex: Int = 30
    private var numberOfModelName = 30
    private var remainingFilteredListSize: Int = 0
    private var remainingListAfterSearchSize: Int = 0
    private lateinit var recyclerAdapter: PhoneModelsAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentModelsBinding.inflate(inflater,container,false)

        binding.searchPhoneModels.clearTextWhenClickClear()
        binding.modelsToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.closeActivityInModelsFragment.setOnClickListener { showDialogOfCloseActivity() }

        getModelNamesFromRoom()

        return binding.root
    }

    private fun getModelNamesFromRoom() {

        launch {

            getPhoneIdFromSharedPref()
            val database = RoomDB(requireContext()).getDataFromRoom()
            val phoneModelNames = database.getModelNamesFromDb() as ArrayList<ModelPhoneModels>
            filterPhoneModelNames(phoneModelNames)
        }
    }

    private fun getPhoneIdFromSharedPref() {

        sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        phoneBrandId = sharedPref?.getString("phoneBrandId","error")
    }

    private fun filterPhoneModelNames(modelNames: ArrayList<ModelPhoneModels>) {

        if (modelNames.isNotEmpty()) {

            filteredList = modelNames.filter { (it.brandId) == phoneBrandId } as ArrayList<ModelPhoneModels>

            if (filteredList.size < numberOfModelName) {

                numberOfModelName = filteredList.size
                binding.moreModelsProgressBar.visibility = View.INVISIBLE

            }else {

                remainingFilteredListSize = filteredList.size
                setMoreModelsRecyclerView(remainingFilteredListSize)
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

    private fun setModelsRecyclerView(list: ArrayList<ModelPhoneModels>) {

        context?.let { context ->

            val recyclerView = binding.modelsRecyclerView
            recyclerView.visibility = View.VISIBLE
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false
            recyclerAdapter = PhoneModelsAdapter(context,list,this@ModelsFragment)
            recyclerView.adapter = recyclerAdapter
        }
    }

    private fun setMoreModelsRecyclerView(_remainingListSize: Int) {

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

                }else { binding.moreModelsProgressBar.visibility = View.INVISIBLE }
            }
        }
    }

    private fun searchInsidePhoneModels() {

        binding.searchPhoneModels.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(text: Editable?) {

                val listAfterSearch = filteredList.filter { list ->

                    (list.modelName.lowercase().contains(text.toString().lowercase())) } as ArrayList<ModelPhoneModels>

                if (listAfterSearch.size < numberOfModelName) {

                    numberOfModelName = listAfterSearch.size
                    binding.moreModelsProgressBar.visibility = View.INVISIBLE

                }else {

                    remainingListAfterSearchSize = listAfterSearch.size
                    setMoreModelsRecyclerView(remainingListAfterSearchSize)
                    binding.moreModelsProgressBar.visibility = View.VISIBLE

                }

                for (index in 0 until numberOfModelName) {

                    limitedListAfterSearch.add(listAfterSearch[index])
                }

                setModelsRecyclerView(limitedListAfterSearch)
            }
        })
    }

    override fun onCardViewClickListener(modelName: String) {

        try {

            saveModelNameAtSharedPref(modelName)
            findNavController().navigate(R.id.action_modelsFragment_to_picturesFragment)

        }catch (e:Exception){}
    }

    private fun saveModelNameAtSharedPref(modelName: String) {

        val editor = sharedPref?.edit()
        editor?.putString("phoneModelName",modelName)
        editor?.apply()
    }

}