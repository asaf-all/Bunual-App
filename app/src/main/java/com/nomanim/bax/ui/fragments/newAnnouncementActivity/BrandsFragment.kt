package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nomanim.bax.R
import com.nomanim.bax.adapters.PhoneBrandsAdapter
import com.nomanim.bax.databinding.FragmentBrandsBinding
import com.nomanim.bax.retrofit.builder.PhoneBrandApi
import com.nomanim.bax.retrofit.listModels.PhoneBrandsList
import com.nomanim.bax.retrofit.models.ModelPhoneBrands
import com.nomanim.bax.ui.activities.MainActivity
import com.nomanim.bax.ui.other.clearTextWhenClickClear
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class BrandsFragment : Fragment(),PhoneBrandsAdapter.Listener {

    private var _binding: FragmentBrandsBinding? = null
    private val binding get() = _binding!!
    private var phoneBrands = ArrayList<ModelPhoneBrands>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        _binding = FragmentBrandsBinding.inflate(inflater,container,false)

        binding.searchPhoneBrands.clearTextWhenClickClear()
        onBackPressed()
        getBrandNamesWithRetrofit()
        binding.brandsToolbar.setNavigationOnClickListener { intentToMainActivity() }

        return binding.root
    }


    private fun getBrandNamesWithRetrofit() {

        val phoneService = PhoneBrandApi.builder.getData()
        phoneService.enqueue(object  : Callback<PhoneBrandsList> {
            override fun onResponse(call: Call<PhoneBrandsList>, response: Response<PhoneBrandsList>?) {

                binding.brandsProgressBar.visibility = View.INVISIBLE

                if (response != null) {

                    try {

                        val model = ModelPhoneBrands("0","Other")
                        phoneBrands.add(model)

                        phoneBrands = response.body()?.modelPhoneBrands as ArrayList<ModelPhoneBrands>
                        setBrandsRecyclerView(phoneBrands)
                        searchInsidePhoneModels()

                    }catch (e: Exception) { context?.let { Toast.makeText(it,R.string.fail,Toast.LENGTH_LONG).show() } }
                }
            }

            override fun onFailure(call: Call<PhoneBrandsList>, t: Throwable) {

                binding.brandsProgressBar.visibility = View.INVISIBLE
            }

        })
    }

    private fun searchInsidePhoneModels() {

        binding.searchPhoneBrands.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(text: Editable?) {

                val listAfterSearch = phoneBrands.filter { list ->

                    (list.brandName.lowercase().contains(text.toString().lowercase())) } as ArrayList<ModelPhoneBrands>

                setBrandsRecyclerView(listAfterSearch)
            }
        })
    }

    private fun setBrandsRecyclerView(list: ArrayList<ModelPhoneBrands>) {

        val brv = binding.brandsRecyclerView
        brv.isNestedScrollingEnabled = false
        context?.let {

            brv.layoutManager = LinearLayoutManager(it)
            brv.setHasFixedSize(true)
            val adapter = PhoneBrandsAdapter(it,list,this@BrandsFragment)
            brv.adapter = adapter
        }
    }

    private fun onBackPressed() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                intentToMainActivity()
            }
        })
    }

    private fun intentToMainActivity() {

        val intent = Intent(activity,MainActivity::class.java)
        activity?.finish()
        activity?.startActivity(intent)

    }

    override fun onCardViewClickListener(brandId: String, brandName: String) {

        try {

            val bundle = Bundle()
            bundle.putString("brandId",brandId)
            bundle.putString("brandName",brandName)
            bundle.putBundle("brandsBundle",bundle)

            findNavController().navigate(R.id.action_brandsFragment_to_modelsFragment,bundle)

        }catch (e: Exception) { context?.let { Toast.makeText(it,"2 item clicked",Toast.LENGTH_SHORT).show() } }

    }

}