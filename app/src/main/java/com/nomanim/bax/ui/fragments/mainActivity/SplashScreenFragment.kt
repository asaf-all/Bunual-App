package com.nomanim.bax.ui.fragments.mainActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentSplashScreenBinding
import com.nomanim.bax.retrofit.builders.PhoneBrandsApi
import com.nomanim.bax.retrofit.builders.PhoneModelsApi
import com.nomanim.bax.retrofit.listModels.PhoneBrandsList
import com.nomanim.bax.retrofit.listModels.PhoneModelsList
import com.nomanim.bax.retrofit.models.ModelPhoneBrands
import com.nomanim.bax.retrofit.models.ModelPhoneModels
import com.nomanim.bax.room.database.RoomDB
import com.nomanim.bax.ui.other.BaseCoroutineScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.collections.ArrayList


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

        firestore.collection("Important Data").document("api_version")
            .get().addOnSuccessListener { value ->

                val activeApiVersionCode = value.get("version_code").toString()
                checkApiVersionCodeForLoadData(activeApiVersionCode)

            }.addOnFailureListener {

                Toast.makeText(requireContext(),"Offline Mode",Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
            }

        navigateIfProcessTakesLongTime()

        return binding.root
    }

    private fun checkApiVersionCodeForLoadData(activeApiVersionCode: String?) {

        val latestApiVersionCode = sharedPref?.getString("api_version_code","1")

        if (activeApiVersionCode == latestApiVersionCode) {

            lifecycleScope.launchWhenResumed {

                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
            }
        }else {

            addNewApiVersionCodeToSharedPref(activeApiVersionCode)
            getBrandNamesWithRetrofit()
        }
    }

    private fun addNewApiVersionCodeToSharedPref(activeApiVersionCode: String?) {

        val editor = sharedPref?.edit()
        editor?.putString("api_version_code",activeApiVersionCode)
        editor?.apply()
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

    private fun getBrandNamesWithRetrofit() {

        val phoneService = PhoneBrandsApi.builder.getData()
        phoneService.enqueue(object  : Callback<PhoneBrandsList> {
            override fun onResponse(call: Call<PhoneBrandsList>, response: Response<PhoneBrandsList>?) {

                if (response != null) {

                    try {

                        val phoneBrands = response.body()?.modelPhoneBrands as ArrayList<ModelPhoneBrands>
                        addPhoneBrandNamesAtRoom(phoneBrands)
                        getModelNamesWithRetrofit()

                    }catch (e: Exception) {

                        context?.let {

                            Toast.makeText(it,R.string.fail, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PhoneBrandsList>, t: Throwable) { }
        })
    }

    private fun getModelNamesWithRetrofit() {

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

                context?.let { Toast.makeText(it,R.string.fail,Toast.LENGTH_SHORT).show() }
                e.localizedMessage
            }
        }
    }

    private fun addPhoneBrandNamesAtRoom(phoneBrands: ArrayList<ModelPhoneBrands>) {

        launch {

            val database = RoomDB(requireContext()).getDataFromRoom()
            database.deleteBrandNames()
            database.insertBrandNames(*phoneBrands.toTypedArray())
        }
    }

    private fun addPhoneModelNamesAtRoom(phoneModels: ArrayList<ModelPhoneModels>) {

        launch {

            val database = RoomDB(requireContext()).getDataFromRoom()
            database.deleteModelNames()
            database.insertModelNames(*phoneModels.toTypedArray())
            findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

}