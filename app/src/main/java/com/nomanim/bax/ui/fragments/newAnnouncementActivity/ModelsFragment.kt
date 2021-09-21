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
import com.nomanim.bax.models.ModelPhone
import com.nomanim.bax.retrofit.builder.PhoneModelApi
import com.nomanim.bax.retrofit.listModels.PhoneModelsList
import com.nomanim.bax.retrofit.models.PhoneModelName
import retrofit2.Call
import retrofit2.Callback

class ModelsFragment : Fragment(),PhoneModelRecyclerView.Listener {

    private var _binding: FragmentModelsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ModelsFragmentArgs>()
    private var filteredList = ArrayList<PhoneModelName>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentModelsBinding.inflate(inflater,container,false)

        binding.modelsToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

        getModelNamesWithRetrofit()
        searchInsidePhoneModels()



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
                        setRecyclerView(limitedAndFilteredList)

                    }catch (e: Exception) {

                        //Toast.makeText(requireContext(),"fail",Toast.LENGTH_SHORT).show()
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
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(text: Editable?) {

                val listAfterSearch = filteredList.filter { list ->

                    (list.modelName.lowercase().contains(text.toString().lowercase())) } as ArrayList<PhoneModelName>

                if (listAfterSearch.size > 1) {

                    val limitList = listAfterSearch.take(100) as ArrayList<PhoneModelName>
                    setRecyclerView(limitList)

                }else { setRecyclerView(listAfterSearch) }
            }
        })
    }

    private fun setRecyclerView(list: ArrayList<PhoneModelName>) {

        val mrv = binding.modelsRecyclerView
        mrv.isNestedScrollingEnabled = false
        mrv.layoutManager = LinearLayoutManager(requireContext())
        mrv.setHasFixedSize(true)
        val adapter = PhoneModelRecyclerView(requireContext(),list,this@ModelsFragment)
        mrv.adapter = adapter
    }

    override fun onCardViewClickListener(modelName: String) {

        try {

            val action = ModelsFragmentDirections.actionModelsFragmentToImagesFragment(args.brandId,modelName)
            findNavController().navigate(action)

        }catch (e: Exception) { Toast.makeText(requireContext(),"***",Toast.LENGTH_SHORT).show() }


    }

}