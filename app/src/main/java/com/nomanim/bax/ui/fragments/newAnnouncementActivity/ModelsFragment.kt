package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nomanim.bax.R
import com.nomanim.bax.adapters.PhoneModelsAdapter
import com.nomanim.bax.databinding.FragmentModelsBinding
import com.nomanim.bax.retrofit.builder.PhoneModelsApi
import com.nomanim.bax.retrofit.listModels.PhoneModelsList
import com.nomanim.bax.retrofit.models.ModelPhoneModels
import com.nomanim.bax.ui.other.clearTextWhenClickClear
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ModelsFragment : Fragment(),PhoneModelsAdapter.Listener {

    private var _binding: FragmentModelsBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable = io.reactivex.disposables.CompositeDisposable()
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
        getModelNamesWithRetrofit()

        return binding.root
    }

    private fun getModelNamesWithRetrofit() {

        compositeDisposable.add(PhoneModelsApi.builder.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseFromRxJava))

    }

    private fun handleResponseFromRxJava(list: PhoneModelsList?) {

        if (list != null) {

            try {

                val phoneModels = list.modelPhoneModels
                filteredList = phoneModels.filter { (it.brandId) == arguments?.getString("brandId") } as ArrayList<ModelPhoneModels>

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

            }catch (e: Exception) {

                context?.let { Toast.makeText(it,R.string.fail,Toast.LENGTH_SHORT).show() }
                e.localizedMessage
            }
        }

        binding.modelsProgressBar.visibility = View.INVISIBLE
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

    private fun setModelsRecyclerView(list: ArrayList<ModelPhoneModels>) {

        val recyclerView = binding.modelsRecyclerView

        context?.let { context ->

            recyclerView.visibility = View.VISIBLE
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false
            recyclerAdapter = PhoneModelsAdapter(context,list,this@ModelsFragment)
            recyclerView.adapter = recyclerAdapter
        }
    }

    override fun onCardViewClickListener(modelName: String) {

        try {

            val bundle = arguments?.getBundle("brandsBundle")
            bundle?.putString("modelName",modelName)
            bundle?.putBundle("modelsBundle",bundle)

            findNavController().navigate(R.id.action_modelsFragment_to_picturesFragment,bundle)

        }catch (e: Exception) { context?.let { Toast.makeText(it,"2 item clicked",Toast.LENGTH_SHORT).show() } }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}