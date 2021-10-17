package com.nomanim.bunual.ui.fragments.newAnnouncementActivity

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
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.adapters.PhoneBrandsAdapter
import com.nomanim.bunual.databinding.FragmentBrandsBinding
import com.nomanim.bunual.retrofit.models.ModelPhoneBrands
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.ui.activities.MainActivity
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import com.nomanim.bunual.ui.other.clearTextWhenClickClear
import com.nomanim.bunual.ui.other.ktx.showDialogOfCloseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class BrandsFragment : BaseCoroutineScope(),PhoneBrandsAdapter.Listener {

    private var _binding: FragmentBrandsBinding? = null
    private val binding get() = _binding!!
    private var phoneBrands = ArrayList<ModelPhoneBrands>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        _binding = FragmentBrandsBinding.inflate(inflater,container,false)

        onBackPressed()
        binding.searchPhoneBrands.clearTextWhenClickClear()
        binding.brandsToolbar.setNavigationOnClickListener { intentToMainActivity() }
        binding.closeActivityInBrandsFragment.setOnClickListener { showDialogOfCloseActivity() }
        getBrandNamesWithRoom()

        return binding.root
    }

    private fun getBrandNamesWithRoom() {

        launch {

            val database = RoomDB(requireContext()).getDataFromRoom()
            phoneBrands = database.getBrandNamesFromDb() as ArrayList<ModelPhoneBrands>
            setBrandsRecyclerView(phoneBrands)
            binding.brandsProgressBar.visibility = View.INVISIBLE
            searchInsidePhoneModels()
        }
    }

    private fun searchInsidePhoneModels() {

        binding.searchPhoneBrands.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(text: Editable?) {

                lifecycleScope.launch {

                    delay(1000)

                    val listAfterSearch = phoneBrands.filter { list ->

                        (list.brandName.lowercase().contains(text.toString().lowercase())) } as ArrayList<ModelPhoneBrands>

                    setBrandsRecyclerView(listAfterSearch)
                    binding.brandsProgressBar.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun setBrandsRecyclerView(list: ArrayList<ModelPhoneBrands>) {

        context?.let {

            val brv = binding.brandsRecyclerView
            brv.isNestedScrollingEnabled = false
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

            val sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.putString("phoneBrandName",brandName)
            editor?.putString("phoneBrandId",brandId)
            editor?.apply()

            findNavController().navigate(R.id.action_brandsFragment_to_modelsFragment)

        }catch (e: Exception) {}
    }

}