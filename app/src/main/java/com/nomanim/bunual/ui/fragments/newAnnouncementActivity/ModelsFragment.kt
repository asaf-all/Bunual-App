package com.nomanim.bunual.ui.fragments.newAnnouncementActivity

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.adapters.PhoneModelsAdapter
import com.nomanim.bunual.databinding.FragmentModelsBinding
import com.nomanim.bunual.models.ModelImages
import com.nomanim.bunual.retrofit.models.ModelPhoneModels
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import com.nomanim.bunual.ui.other.clearTextWhenClickClear
import com.nomanim.bunual.ui.other.ktx.showDialogOfCloseActivity
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ModelsFragment : BaseCoroutineScope(),PhoneModelsAdapter.Listener {

    private var _binding: FragmentModelsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ModelsFragmentArgs>()
    private var sharedPref: SharedPreferences? = null
    private var phoneBrandId: String? = null
    private var filteredList = ArrayList<ModelPhoneModels>()
    private val limitedAndFilteredList = ArrayList<ModelPhoneModels>()
    private val limitedListAfterSearch = ArrayList<ModelPhoneModels>()
    private val imagesUrl = ArrayList<Uri>()
    private var lastLoudIndex: Int = 30
    private var numberOfModelName = 30
    private var numberOfSearchedModelName = 20
    private var remainingFilteredListSize: Int = 0
    private var remainingListAfterSearchSize: Int = 0
    private lateinit var recyclerAdapter: PhoneModelsAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentModelsBinding.inflate(inflater,container,false)
        sharedPref = activity?.getSharedPreferences("sharedPrefInNewAdsActivity",Context.MODE_PRIVATE)

        pressBackButton()

        binding.searchPhoneModels.clearTextWhenClickClear()
        binding.modelsToolbar.setNavigationOnClickListener { navigateToPreviousFragment() }
        binding.closeActivityInModelsFragment.setOnClickListener { showDialogOfCloseActivity() }

        if (args.fromDescriptionFragment) {

            getImagesUrlIfIsNotEmptyInRoom()
            lifecycleScope.launch { getModelNamesFromRoom() }

        } else { getModelNamesFromRoom()

            launch {

                val database = RoomDB(requireContext()).getDataFromRoom()
                database.deleteImagesUri()
            }
        }
        return binding.root
    }

    private fun getModelNamesFromRoom() {

        launch {

            phoneBrandId = sharedPref?.getString("phoneBrandId",null)
            val database = RoomDB(requireContext()).getDataFromRoom()
            val phoneModelNames = database.getModelNamesFromDb() as ArrayList<ModelPhoneModels>
            filterPhoneModelNames(phoneModelNames)
        }
    }


    private fun filterPhoneModelNames(modelNames: ArrayList<ModelPhoneModels>) {

        if (modelNames.isNotEmpty()) {

            filteredList = modelNames.filter { (it.brandId) == phoneBrandId } as ArrayList<ModelPhoneModels>

            if (filteredList.size < numberOfModelName) {

                numberOfModelName = filteredList.size
                binding.moreModelsProgressBar.visibility = View.INVISIBLE

            }else {

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

                }else { binding.moreModelsProgressBar.visibility = View.INVISIBLE }
            }
        }
    }

    private fun searchInsidePhoneModels() {

        binding.searchPhoneModels.addTextChangedListener( object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(text: Editable?) {

                lifecycleScope.launch {

                    delay(1000)

                    val listAfterSearch = filteredList.filter { list ->

                        (list.modelName.lowercase().contains(text.toString().lowercase())) } as ArrayList<ModelPhoneModels>


                    if (listAfterSearch.size < numberOfSearchedModelName) {

                        numberOfSearchedModelName = listAfterSearch.size
                        binding.moreModelsProgressBar.visibility = View.INVISIBLE

                    }else {

                        remainingListAfterSearchSize = listAfterSearch.size
                        addMoreModelNamesAtRecyclerView(remainingListAfterSearchSize)
                        binding.moreModelsProgressBar.visibility = View.VISIBLE
                    }

                    limitedListAfterSearch.clear()

                    for (index in 0 until numberOfSearchedModelName) {

                        limitedListAfterSearch.add(listAfterSearch[index])
                    }

                    setModelsRecyclerView(limitedListAfterSearch)
                }
            }
        })
    }

    override fun onCardViewClickListener(modelName: String) {

        try {

            saveModelNameAtSharedPref(modelName)
            getImagesUrlIfIsNotEmptyInRoom()

        }catch (e:Exception){}
    }

    private fun saveModelNameAtSharedPref(modelName: String) {

        val editor = sharedPref?.edit()
        editor?.putString("phoneModelName",modelName)
        editor?.apply()
    }

    private fun getImagesUrlIfIsNotEmptyInRoom() {

        launch {

            val imagesUrlAsModel = RoomDB(requireContext()).getDataFromRoom().getImagesUriFromDb()

            for (index in imagesUrlAsModel.indices) {

                imagesUrl.add(imagesUrlAsModel[index].imageUri.toUri())
            }
            openGallery()
        }
    }

    private fun openGallery() {

        TedImagePicker.with(requireContext())
            .title(R.string.select_phone_image)
            .backButton(R.drawable.back)
            .buttonText(R.string.next)
            .buttonBackground(R.color.main)
            .mediaType(MediaType.IMAGE)
            .selectedUri(imagesUrl)
            .startMultiImage { imagesUri ->

                val list = ArrayList<ModelImages>()

                for (element in imagesUri) {

                    val model = ModelImages(element.toString())
                    list.add(model)
                }

                saveImagesUrlAtRoom(list)
            }
    }

    private fun saveImagesUrlAtRoom(list: ArrayList<ModelImages>) {

        launch {

            val database = RoomDB(requireContext()).getDataFromRoom()
            database.deleteImagesUri()
            database.insertImagesUri(*list.toTypedArray())
            findNavController().navigate(R.id.action_modelsFragment_to_descriptionFragment)
        }
    }

    private fun navigateToPreviousFragment() {

        findNavController().navigate(R.id.action_modelsFragment_to_brandsFragment)
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
        job.cancel()
    }

}