package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nomanim.bax.R
import com.nomanim.bax.adapters.PhoneBrandRecyclerView
import com.nomanim.bax.databinding.FragmentBrandsBinding
import com.nomanim.bax.retrofit.builder.PhoneBrandApi
import com.nomanim.bax.retrofit.listModels.PhoneBrandsList
import com.nomanim.bax.retrofit.models.PhoneBrandName
import com.nomanim.bax.ui.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class BrandsFragment : Fragment(),PhoneBrandRecyclerView.Listener {

    private var _binding: FragmentBrandsBinding? = null
    private val binding get() = _binding!!
    private var phoneBrands = ArrayList<PhoneBrandName>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        _binding = FragmentBrandsBinding.inflate(inflater,container,false)

        getBrandNamesWithRetrofit()
        onBackPressed()
        binding.brandsToolbar.setNavigationOnClickListener { intentToMainActivity() }

        return binding.root
    }

    private fun getBrandNamesWithRetrofit() {

        val phoneService = PhoneBrandApi.buildAndCreate()
        phoneService.getData().enqueue(object  : Callback<PhoneBrandsList> {
            override fun onResponse(call: Call<PhoneBrandsList>, response: Response<PhoneBrandsList>?) {

                binding.brandsProgressBar.visibility = View.INVISIBLE

                if (response != null) {

                    try {

                        phoneBrands = response.body()?.phoneBrandNames as ArrayList<PhoneBrandName>
                        setBrandsRecyclerView(phoneBrands)

                    }catch (e: Exception) { Toast.makeText(requireContext(),getString(R.string.all_announcements),Toast.LENGTH_LONG).show() }
                }
            }

            override fun onFailure(call: Call<PhoneBrandsList>, t: Throwable) {

                binding.brandsProgressBar.visibility = View.INVISIBLE
            }

        })
    }

    private fun setBrandsRecyclerView(list: ArrayList<PhoneBrandName>) {

        val brv = binding.brandsRecyclerView
        brv.isNestedScrollingEnabled = false
        brv.layoutManager = LinearLayoutManager(requireContext())
        brv.setHasFixedSize(true)
        val adapter = PhoneBrandRecyclerView(requireContext(),list,this@BrandsFragment)
        brv.adapter = adapter
    }

    private fun onBackPressed() {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                intentToMainActivity()
            }
        })
    }

    private fun intentToMainActivity() {

        val intent = Intent(requireActivity(),MainActivity::class.java)
        requireActivity().finish()
        requireActivity().startActivity(intent)

    }

    override fun onCardViewClickListener(brandId: String) {

        try {

            val action = BrandsFragmentDirections.actionBrandsFragmentToModelsFragment(brandId)
            findNavController().navigateUp()
            findNavController().navigate(action)

        }catch (e: Exception) { Toast.makeText(requireContext(),"***",Toast.LENGTH_SHORT).show() }

    }

}