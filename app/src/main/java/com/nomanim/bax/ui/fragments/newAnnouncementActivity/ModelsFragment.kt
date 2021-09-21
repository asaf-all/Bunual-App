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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nomanim.bax.R
import com.nomanim.bax.adapters.PhoneModelRecyclerView
import com.nomanim.bax.databinding.FragmentModelsBinding
import com.nomanim.bax.retrofit.builder.PhoneModelApi
import com.nomanim.bax.retrofit.listModels.PhoneModelsList
import com.nomanim.bax.retrofit.models.PhoneModelName
import com.nomanim.bax.ui.other.ClearEditTextButton
import retrofit2.Call
import retrofit2.Callback

class ModelsFragment : Fragment(),PhoneModelRecyclerView.Listener {

    private var _binding: FragmentModelsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ModelsFragmentArgs>()
    private var filteredList = ArrayList<PhoneModelName>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentModelsBinding.inflate(inflater,container,false)

        ClearEditTextButton(binding.searchPhoneModels)
        binding.modelsToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        getModelNamesWithRetrofit()

        return binding.root
    }

    private fun getModelNamesWithRetrofit() {

        val phoneService = PhoneModelApi.buildAndCreate()
        phoneService.getData().enqueue(object  : Callback<PhoneModelsList> {
            override fun onResponse(call: Call<PhoneModelsList>, response: retrofit2.Response<PhoneModelsList>?) {

                binding.modelsProgressBar.visibility = View.INVISIBLE

                if (response != null) {

                    try {

                        val phoneModels = response.body()?.phoneModelNames as ArrayList<PhoneModelName>
                        filteredList = phoneModels.filter { (it.brandId) == args.brandId } as ArrayList<PhoneModelName>
                        val limitedAndFilteredList = filteredList.take(50) as ArrayList<PhoneModelName>
                        setModelsRecyclerView(limitedAndFilteredList)
                        searchInsidePhoneModels()

                    }catch (e: Exception) {

                        context?.let { Toast.makeText(it,R.string.fail,Toast.LENGTH_SHORT).show() }
                        e.localizedMessage
                    }
                }
            }
            override fun onFailure(call: Call<PhoneModelsList>, t: Throwable) {

                binding.modelsProgressBar.visibility = View.INVISIBLE
            }
        })
    }

    private fun searchInsidePhoneModels() {

        binding.searchPhoneModels.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {

                val listAfterSearch = filteredList.filter { list ->

                    (list.modelName.lowercase().contains(text.toString().lowercase())) } as ArrayList<PhoneModelName>

                if (listAfterSearch.size > 1) {

                    val limitList = listAfterSearch.take(100) as ArrayList<PhoneModelName>
                    setModelsRecyclerView(limitList)

                }else { setModelsRecyclerView(listAfterSearch) }
            }

            override fun afterTextChanged(text: Editable?) { }
        })
    }

    private fun setModelsRecyclerView(list: ArrayList<PhoneModelName>) {

        val mrv = binding.modelsRecyclerView
        mrv.isNestedScrollingEnabled = false
        context?.let { context ->

            mrv.layoutManager = LinearLayoutManager(context)
            mrv.setHasFixedSize(true)
            val adapter = PhoneModelRecyclerView(context,list,this@ModelsFragment)
            mrv.adapter = adapter
        }
    }

    override fun onCardViewClickListener(modelName: String) {

        try {

            val action = ModelsFragmentDirections.actionModelsFragmentToİmagesFragment(args.brandName,modelName)
            findNavController().navigate(action)

        }catch (e: Exception) { context?.let { Toast.makeText(it,"2 item clicked",Toast.LENGTH_SHORT).show() } }


    }

}