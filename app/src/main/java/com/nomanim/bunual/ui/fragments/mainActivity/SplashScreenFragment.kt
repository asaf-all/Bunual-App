package com.nomanim.bunual.ui.fragments.mainActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentSplashScreenBinding
import com.nomanim.bunual.retrofit.builders.PhoneBrandsApi
import com.nomanim.bunual.retrofit.builders.PhoneModelsApi
import com.nomanim.bunual.retrofit.builders.SimpleDataApi
import com.nomanim.bunual.retrofit.listModels.PhoneBrandsList
import com.nomanim.bunual.retrofit.listModels.PhoneModelsList
import com.nomanim.bunual.retrofit.models.ModelPhoneBrands
import com.nomanim.bunual.retrofit.models.ModelPhoneModels
import com.nomanim.bunual.retrofit.models.ModelSimpleData
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : BaseCoroutineScope() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private var sharedPref: SharedPreferences? = null
    private val compositeDisposable = io.reactivex.disposables.CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentSplashScreenBinding.inflate(inflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        firestore = FirebaseFirestore.getInstance()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
        sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)
        checkTimeInSharedPreferences()
    }

    private fun checkTimeInSharedPreferences() {

        val currentTime  = Calendar.getInstance().get(Calendar.MONTH)
        val savedTime = sharedPref?.getString("timeWithMonth",(currentTime- 1).toString())

        if (savedTime == currentTime.toString()) {

            lifecycleScope.launch {

                binding.splashScreenProgressBar.visibility = View.INVISIBLE
                delay(1000)
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment) }

        }else {

            getActiveApiVersionCode()
        }

        updateTimeInSharedPreferences(currentTime)
    }



    private fun getActiveApiVersionCode() {

        firestore.collection("Important Data").document("api_version")
            .get().addOnSuccessListener { value ->

                val activeApiVersionCode = value.get("version_code").toString()
                checkApiVersionCodeForLoadData(activeApiVersionCode)

            }.addOnFailureListener { error ->

                Toast.makeText(requireContext(),R.string.no_internet_connection,Toast.LENGTH_LONG).show()
                error.localizedMessage
            }
    }

    private fun checkApiVersionCodeForLoadData(activeApiVersionCode: String?) {

        val latestApiVersionCode = sharedPref?.getString("api_version_code","0")

        if (activeApiVersionCode == latestApiVersionCode) {

            lifecycleScope.launchWhenResumed {

                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
            }
        }else {

            binding.downloadingTextView.visibility = View.VISIBLE
            updateApiVersionCodeInSharedPref(activeApiVersionCode)
            getBrandNamesWithRetrofit()
        }
    }

    private fun updateTimeInSharedPreferences(currentTime: Int) {

        val editor = sharedPref?.edit()
        editor?.putString("timeWithMonth",currentTime.toString())
        editor?.apply()
    }

    private fun updateApiVersionCodeInSharedPref(activeApiVersionCode: String?) {

        val editor = sharedPref?.edit()
        editor?.putString("api_version_code",activeApiVersionCode)
        editor?.apply()
    }

    private fun getBrandNamesWithRetrofit() {

        val phoneService = PhoneBrandsApi.builder.getData()
        phoneService.enqueue(object  : Callback<PhoneBrandsList> {
            override fun onResponse(call: Call<PhoneBrandsList>, response: Response<PhoneBrandsList>?) {

                if (response != null) {

                    try {

                        val phoneBrands = response.body()?.modelPhoneBrands as ArrayList<ModelPhoneBrands>
                        addPhoneBrandNamesAtRoom(phoneBrands)
                        getModelNamesWithRxJava()

                    } catch (e: Exception) {

                        context?.let { Toast.makeText(it, R.string.no_internet_connection, Toast.LENGTH_LONG).show() }
                    }
                }
            }

            override fun onFailure(call: Call<PhoneBrandsList>, t: Throwable) {

                Toast.makeText(requireContext(), R.string.fail, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getModelNamesWithRxJava() {

        compositeDisposable.add(
            PhoneModelsApi.builder.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseFromRxJava))
    }

    private fun handleResponseFromRxJava(list: PhoneModelsList?) {

        if (list != null) {

            try {

                val phoneModels = list.modelPhoneModels as ArrayList<ModelPhoneModels>
                addPhoneModelNamesAtRoom(phoneModels)

            }catch (e: Exception) {

                context?.let { Toast.makeText(it,R.string.no_internet_connection,Toast.LENGTH_SHORT).show() }
                e.localizedMessage
            }
        }
    }

    private fun addPhoneBrandNamesAtRoom(phoneBrands: ArrayList<ModelPhoneBrands>) {

        launch {

            context?.let {

                val database = RoomDB(it).getDataFromRoom()
                database.deleteBrandNames()
                database.insertBrandNames(*phoneBrands.toTypedArray())
            }
        }
    }

    private fun addPhoneModelNamesAtRoom(phoneModels: ArrayList<ModelPhoneModels>) {

        launch {

            context?.let {

                val database = RoomDB(it).getDataFromRoom()
                database.deleteModelNames()
                database.insertModelNames(*phoneModels.toTypedArray())
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
            }
        }
    }

    private fun checkInternetConnection() {

        val phoneService = SimpleDataApi.builder.getData()
        phoneService.enqueue(object  : Callback<ModelSimpleData> {
            override fun onResponse(call: Call<ModelSimpleData>, response: Response<ModelSimpleData>?) {}
            override fun onFailure(call: Call<ModelSimpleData>, t: Throwable) {}
        })
    }

    private fun navigateIfProcessTakesLongTime() {

        object : CountDownTimer(20000,1000) {

            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {

                Toast.makeText(requireContext(),"Offline Mode",Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
        compositeDisposable.clear()
    }

}