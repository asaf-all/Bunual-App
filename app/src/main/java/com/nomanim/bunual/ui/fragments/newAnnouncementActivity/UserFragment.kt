package com.nomanim.bunual.ui.fragments.newAnnouncementActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentUserBinding
import com.nomanim.bunual.models.ModelImages
import com.nomanim.bunual.retrofit.builders.PlacesApi
import com.nomanim.bunual.retrofit.models.ModelPlaces
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import com.nomanim.bunual.ui.other.ktx.showFeaturesBottomSheet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class UserFragment : BaseCoroutineScope() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private var sharedPref: SharedPreferences? = null
    private val compositeDisposable = CompositeDisposable()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var placesList: List<ModelPlaces>
    private lateinit var imagesUrl: List<ModelImages>
    private val downloadUrlList = ArrayList<String>()
    private var savedUserName: String = ""
    private var savedPhoneNumber: String = ""
    private var savedPlaceName: String = ""
    private var deliveryStatus: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentUserBinding.inflate(inflater)
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        sharedPref = activity?.getSharedPreferences("sharedPrefInNewAdsActivity", Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedPlaceName = sharedPref?.getString("placeName",getString(R.string.choose_place)).toString()
        savedUserName = sharedPref?.getString("userName","").toString()
        savedPhoneNumber = auth.currentUser?.phoneNumber.toString()

        lifecycleScope.launch { getPlacesNameFromInternet() }
        checkRadioGroupOfDeliveryStatus()
        pressBackButton()
        binding.userToolbar.setNavigationOnClickListener { navigateToPreviousFragment() }
        getAllDataForUploadToFirestore()
        getImagesUrlFromRoom()
    }

    private fun getPlacesNameFromInternet() {

        binding.placeTextView.text = getString(R.string.places_name_downloading)
        binding.userNameEditText.setText(savedUserName)
        binding.userPhoneNumberEditText.setText(savedPhoneNumber)

        compositeDisposable.add(
            PlacesApi.builder.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<ModelPlaces>>() {

                    override fun onSuccess(list: List<ModelPlaces>) {

                        binding.placeTextView.text = savedPlaceName
                        placesList = list.sortedWith(compareByDescending { it.population })
                        binding.userPlaceCardView.setOnClickListener {

                            val listForDialog = ArrayList<String>()
                            for (element in placesList) { listForDialog.add(element.city) }
                            showFeaturesBottomSheet(listForDialog,binding.placeTextView,getString(R.string.choose_place)) }
                    }

                    override fun onError(error: Throwable) {

                        Snackbar.make(binding.root,getString(R.string.fail), Snackbar.LENGTH_SHORT).show()
                        error.printStackTrace()
                    }
                }))
    }

    private fun checkRadioGroupOfDeliveryStatus() {

        binding.deliveryRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.noDeliveryRadioButton -> { deliveryStatus = getString(R.string.no_delivery) }
                R.id.deliveryToPlaceRadioButton -> { deliveryStatus = getString(R.string.delivery_to_place) }
                R.id.subwayDeliveryRadioButton -> { deliveryStatus = getString(R.string.subway_delivery) }
            }
        }

    }

    private fun getAllDataForUploadToFirestore() {

        val phoneBrandName = sharedPref?.getString("phoneBrandName",null)
        val phoneModelName = sharedPref?.getString("phoneModelName",null)
        val description = sharedPref?.getString("description",null)
        val storageCapacity = sharedPref?.getString("storageCapacity",null)
        val ramCapacity = sharedPref?.getString("ramCapacity",null)
        val color = sharedPref?.getString("color",null)
        val status = sharedPref?.getString("status",null)
        val price = sharedPref?.getString("price",null) + "AZN"

        val place = binding.placeTextView.text
        val userName = binding.userNameEditText.text
        val userPhoneNumber = binding.userPhoneNumberEditText.text
        val delivery = deliveryStatus
    }

    private fun getImagesUrlFromRoom() {

        launch {

            imagesUrl = RoomDB(requireContext()).getDataFromRoom().getImagesUriFromDb()
            binding.shareAdsButton.setOnClickListener { /*uploadImagesToStorage(imagesUrl)*/ }
        }
    }

    private fun uploadImagesToStorage(imagesList: List<ModelImages>) {

        val reference = firebaseStorage.reference

        for (i in imagesList.indices) {

            val child = reference.child("Pictures").child(UUID.randomUUID().toString())
            val uploadTask = child.putFile(imagesList[i].imageUri.toUri()).addOnSuccessListener {

                downloadUrlList.add(child.downloadUrl.toString())
            }
        }
    }

    private fun uploadAnnouncementToFirestore() {

        val editor = sharedPref?.edit()
        editor?.putString("userName",binding.userNameEditText.text.toString())
        editor?.apply()
    }

    private fun loadingProgressBarInDialog() {

        KProgressHUD.create(requireContext())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("Downloading data")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.3f)
            .show()
    }

    private fun navigateToPreviousFragment() {

        val editor = sharedPref?.edit()
        editor?.putString("userName",binding.userNameEditText.text.toString())
        editor?.apply()
        findNavController().navigate(R.id.action_userFragment_to_priceFragment)
    }

    private fun pressBackButton() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                navigateToPreviousFragment()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        job.cancel()
    }
}