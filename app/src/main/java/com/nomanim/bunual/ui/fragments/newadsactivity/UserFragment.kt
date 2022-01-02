package com.nomanim.bunual.ui.fragments.newadsactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentUserBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.models.ModelImages
import com.nomanim.bunual.models.ModelPhone
import com.nomanim.bunual.models.ModelUser
import com.nomanim.bunual.api.entity.ModelPlaces
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.ui.activities.MainActivity
import com.nomanim.bunual.base.BaseCoroutineScope
import com.nomanim.bunual.extensions.loadingProgressBarInDialog
import com.nomanim.bunual.extensions.showFeaturesBottomSheet
import com.nomanim.bunual.viewmodel.UserViewModel
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class UserFragment : BaseCoroutineScope() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()
    private var sharedPref: SharedPreferences? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var modelAnnouncement: ModelAnnouncement
    private lateinit var placesList: List<ModelPlaces>
    private lateinit var imagesUrl: List<ModelImages>
    private val downloadUrlList = ArrayList<String>()
    private var savedUserName: String = ""
    private var savedPhoneNumber: String = ""
    private var savedPlaceName: String = ""
    private var deliveryStatus: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        sharedPref =
            activity?.getSharedPreferences("sharedPrefInNewAdsActivity", Context.MODE_PRIVATE)
        savedPlaceName =
            sharedPref?.getString("placeName", getString(R.string.choose_place)).toString()
        savedUserName = sharedPref?.getString("userName", "").toString()
        savedPhoneNumber = auth.currentUser?.phoneNumber.toString()

        initUserViewModel()
        binding.placeTextView.text = getString(R.string.places_name_downloading)
        binding.userNameEditText.setText(savedUserName)
        binding.userPhoneNumberEditText.setText(savedPhoneNumber)
        mUserViewModel.getPlaces()

        checkRadioGroupOfDeliveryStatus()
        pressBackButton()
        binding.userToolbar.setNavigationOnClickListener { navigateToPreviousFragment() }
        getImagesUrlFromRoom()
    }

    private fun initUserViewModel() {
        mUserViewModel.placesLiveData().observe(viewLifecycleOwner, { response ->
            binding.placeTextView.text = savedPlaceName
            placesList = response.sortedWith(compareByDescending { it.population })
            binding.userPlaceCardView.setOnClickListener {
                val listForDialog = ArrayList<String>()
                for (element in placesList) {
                    listForDialog.add(element.city)
                }
                showFeaturesBottomSheet(
                    listForDialog,
                    binding.placeTextView,
                    getString(R.string.choose_place),
                    true
                )
            }
        })
        mUserViewModel.errorMutableLiveData.observe(viewLifecycleOwner, { message ->
            ToastUtil.showToast("error: $message")
        })
    }

    private fun checkRadioGroupOfDeliveryStatus() {
        binding.deliveryRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.noDeliveryRadioButton -> {
                    deliveryStatus = getString(R.string.no_delivery)
                }
                R.id.deliveryToPlaceRadioButton -> {
                    deliveryStatus = getString(R.string.delivery_to_place)
                }
                R.id.subwayDeliveryRadioButton -> {
                    deliveryStatus = getString(R.string.subway_delivery)
                }
            }
        }
    }

    private fun getImagesUrlFromRoom() {
        launch {
            imagesUrl = RoomDB(requireContext()).getDataFromRoom().getImagesUriFromDb()
            //binding.shareAdsButton.setOnClickListener { uploadImagesToStorage(imagesUrl) }
        }
    }

    private fun uploadImagesToStorage(imagesList: List<ModelImages>) {

        val dialog = loadingProgressBarInDialog(
            getString(R.string.download), getString(R.string.wait), false
        )
        dialog.show()

        val reference = firebaseStorage.reference

        for (image in imagesList) {
            val child = reference.child("Pictures").child(UUID.randomUUID().toString())
            child.putFile(image.imageUri.toUri()).addOnSuccessListener {
                child.downloadUrl.addOnSuccessListener { uri ->
                    downloadUrlList.add(uri.toString())
                    Log.e("******", downloadUrlList.toString())
                }
            }
        }
        getAllDataForUploadToFirestore()
        uploadAdsToFirestore(dialog)
    }

    private fun getAllDataForUploadToFirestore() {
        val brandName = sharedPref?.getString("phoneBrandName", null)
        val modelName = sharedPref?.getString("phoneModelName", null)
        val description = sharedPref?.getString("description", null)
        val storage = sharedPref?.getString("storageCapacity", null)
        val ram = sharedPref?.getString("ramCapacity", null)
        val color = sharedPref?.getString("color", null)
        val status = sharedPref?.getString("status", null)
        val price = sharedPref?.getString("price", null) + "AZN"

        val city = binding.placeTextView.text.toString()
        val userName = binding.userNameEditText.text.toString()
        val phoneNumber = binding.userPhoneNumberEditText.text.toString()
        val delivery = deliveryStatus

        if (brandName != null && modelName != null && description != null && storage != null &&
            ram != null && color != null && status != null
        ) {
            val modelPhone = ModelPhone(
                brandName,
                modelName,
                price,
                color,
                storage,
                ram,
                status,
                delivery,
                false
            )
            val modelPlaces = ModelPlaces(city, "not important")
            val modelUser = ModelUser(userName, phoneNumber, modelPlaces)

            modelAnnouncement = ModelAnnouncement(
                "",
                auth.currentUser?.phoneNumber.toString(),
                downloadUrlList,
                description,
                "0",
                Timestamp.now(),
                modelPhone,
                modelUser
            )
        }
    }

    private fun uploadAdsToFirestore(loadingProgressBarInDialog: KProgressHUD) {
        val editor = sharedPref?.edit()
        editor?.putString("userName", binding.userNameEditText.text.toString())
        editor?.apply()
        firestore.collection("All Announcements").add(modelAnnouncement).addOnSuccessListener {

            val intent = Intent(activity, MainActivity::class.java)
            activity?.finish()
            activity?.startActivity(intent)
            activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            loadingProgressBarInDialog.dismiss()
        }
    }

    private fun navigateToPreviousFragment() {
        val editor = sharedPref?.edit()
        editor?.putString("userName", binding.userNameEditText.text.toString())
        editor?.apply()
        findNavController().navigate(R.id.action_userFragment_to_priceFragment)
    }

    private fun pressBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateToPreviousFragment()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}