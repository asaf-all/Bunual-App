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
import androidx.work.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.CheckVersionWork
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentSplashScreenBinding
import com.nomanim.bunual.api.entity.BrandsResponse
import com.nomanim.bunual.api.entity.ModelsResponse
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.base.BaseCoroutineScope
import com.nomanim.bunual.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
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
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        firestore = FirebaseFirestore.getInstance()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
        sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        initSplashViewModel()

        val checkState = sharedPref?.getBoolean("check_api_version", true)

        if (checkState != null && checkState) {
            mSplashViewModel.getApiVersionCode(firestore)
        } else {

            val workConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<CheckVersionWork>(1, TimeUnit.DAYS)
                .setConstraints(workConstraints)
                .build()
            WorkManager.getInstance(requireContext()).enqueue(workRequest)

            lifecycleScope.launchWhenCreated {
                delay(500)
                navigateToNextFragment()
            }
        }
    }

    private fun initSplashViewModel() {
        mSplashViewModel.apiVersionLiveData().observe(viewLifecycleOwner) { response ->
            currentApiVersionCode = response.get("version_code").toString()
            checkApiVersionCode(currentApiVersionCode)
        }
        mSplashViewModel.brandsLiveData().observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val brandsList = response.body as ArrayList<BrandsResponse.Body>
                addBrandsNamesToRoom(brandsList)
            }
        }
        mSplashViewModel.modelsLiveData().observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val modelsList = response.body as ArrayList<ModelsResponse.Body>
                addModelsNamesToRoom(modelsList)
            }
        }
        mSplashViewModel.errorMutableLiveData.observe(viewLifecycleOwner) { message ->
            binding.splashScreenProgressBar.visibility = View.INVISIBLE
            showToastMessage("error: $message")
            Log.e("com.nomanim.bunual", message.toString())
        }
    }

    private fun checkApiVersionCode(activeApiVersionCode: String?) {
        val latestApiVersionCode = sharedPref?.getString("api_version_code", "0")
        if (activeApiVersionCode == latestApiVersionCode) {
            navigateToNextFragment()
        } else {
            binding.downloadingTextView.visibility = View.VISIBLE
            mSplashViewModel.getPhoneBrands()
        }
    }

    private fun addBrandsNamesToRoom(brandsList: ArrayList<BrandsResponse.Body>) {
        launch {
            val database = RoomDB(requireContext()).getDataFromRoom()
            database.deleteBrandNames()
            database.insertBrandNames(*brandsList.toTypedArray())
            mSplashViewModel.getPhoneModels()
        }
    }

    private fun addModelsNamesToRoom(modelsList: ArrayList<ModelsResponse.Body>) {
        launch {
            val database = RoomDB(requireContext()).getDataFromRoom()
            database.deleteModelNames()
            database.insertModelNames(*modelsList.toTypedArray())

            val editor = sharedPref?.edit()
            editor?.putString("api_version_code", currentApiVersionCode)
            editor?.apply()
            navigateToNextFragment()
        }
    }

    private fun navigateToNextFragment() {
        sharedPref?.edit()?.putBoolean("check_api_version", false)?.apply()
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