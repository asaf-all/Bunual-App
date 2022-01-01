package com.nomanim.bunual.ui.fragments.mainactivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentSplashScreenBinding
import com.nomanim.bunual.api.entity.ModelPhoneBrands
import com.nomanim.bunual.api.entity.ModelPhoneModels
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.base.BaseCoroutineScope
import com.nomanim.bunual.viewmodel.SplashViewModel
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : BaseCoroutineScope() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private val mSplashViewModel: SplashViewModel by viewModels()
    private lateinit var firestore: FirebaseFirestore
    private var sharedPref: SharedPreferences? = null

    private var currentApiVersionCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        firestore = FirebaseFirestore.getInstance()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
        sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        initSplashViewModel()
        mSplashViewModel.getApiVersionCode(firestore)
    }

    private fun initSplashViewModel() {
        mSplashViewModel.apiVersionLiveData().observe(viewLifecycleOwner, { response ->
            currentApiVersionCode = response.get("version_code").toString()
            checkApiVersionCodeForLoadData(currentApiVersionCode)

        })
        mSplashViewModel.brandsLiveData().observe(viewLifecycleOwner, { response ->
            if (response != null) {
                val phoneBrands = response.modelPhoneBrands as ArrayList<ModelPhoneBrands>
                addPhoneBrandNamesToRoom(phoneBrands)
            }
        })
        mSplashViewModel.modelsLiveData().observe(viewLifecycleOwner, { response ->
            val phoneModels = response.modelPhoneModels as ArrayList<ModelPhoneModels>
            addPhoneModelNamesToRoom(phoneModels)
        })
        mSplashViewModel.errorMutableLiveData.observe(viewLifecycleOwner, { message ->
            ToastUtil.showToast("error: $message")
        })
    }

    private fun checkApiVersionCodeForLoadData(activeApiVersionCode: String?) {
        val latestApiVersionCode = sharedPref?.getString("api_version_code", "0")
        if (activeApiVersionCode == latestApiVersionCode) {
            lifecycleScope.launchWhenResumed {
                navigateToNextFragment()
            }
        } else {
            binding.downloadingTextView.visibility = View.VISIBLE
            mSplashViewModel.getPhoneBrands()
        }
    }

    private fun addPhoneBrandNamesToRoom(phoneBrands: ArrayList<ModelPhoneBrands>) {
        launch {
            val database = RoomDB(requireContext()).getDataFromRoom()
            database.deleteBrandNames()
            database.insertBrandNames(*phoneBrands.toTypedArray())
            mSplashViewModel.getPhoneModels()
        }
    }

    private fun addPhoneModelNamesToRoom(phoneModels: ArrayList<ModelPhoneModels>) {
        launch {
            val database = RoomDB(requireContext()).getDataFromRoom()
            database.deleteModelNames()
            database.insertModelNames(*phoneModels.toTypedArray())

            val editor = sharedPref?.edit()
            editor?.putString("api_version_code", currentApiVersionCode)
            editor?.apply()
            navigateToNextFragment()
        }
    }

    private fun navigateToNextFragment() {
        findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
    }

    private fun navigateIfProcessTakesLongTime() {
        object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                navigateToNextFragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }

}