package com.nomanim.bunual.ui.fragments.newadsactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.nomanim.bunual.adapters.PhoneBrandsAdapter
import com.nomanim.bunual.databinding.FragmentBrandsBinding
import com.nomanim.bunual.api.entity.ModelPhoneBrands
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.ui.activities.MainActivity
import com.nomanim.bunual.base.BaseCoroutineScope
import com.nomanim.bunual.extensions.showDialogOfCloseActivity
import com.nomanim.bunual.base.clearTextWhenClickClear
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class BrandsFragment : BaseCoroutineScope(),PhoneBrandsAdapter.Listener {

    private var _binding: FragmentBrandsBinding? = null
    private val binding get() = _binding!!
    private var sharedPref: SharedPreferences? = null
    private var phoneBrands = ArrayList<ModelPhoneBrands>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        _binding = FragmentBrandsBinding.inflate(inflater,container,false)
        sharedPref = activity?.getSharedPreferences("sharedPrefInNewAdsActivity",Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed()
        binding.brandsToolbar.setNavigationOnClickListener { intentToMainActivity() }
        binding.closeActivityInBrandsFragment.setOnClickListener { showDialogOfCloseActivity() }
        binding.searchPhoneBrands.clearTextWhenClickClear()

        getBrandNamesWithRoom()
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
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCardViewClickListener(brandId: String, brandName: String) {
        try {
            val editor = sharedPref?.edit()
            editor?.putString("phoneBrandName",brandName)
            editor?.putString("phoneBrandId",brandId)
            editor?.apply()

            val action = BrandsFragmentDirections.actionBrandsFragmentToModelsFragment(true)
            findNavController().navigate(action)

        }catch (e: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}