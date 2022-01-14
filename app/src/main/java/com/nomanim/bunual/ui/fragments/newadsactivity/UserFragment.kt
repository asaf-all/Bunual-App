package com.nomanim.bunual.ui.fragments.newadsactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.nomanim.bunual.models.ModelPhone
import com.nomanim.bunual.models.ModelUser
import com.nomanim.bunual.api.entity.ModelPlaces
import com.nomanim.bunual.ui.activities.MainActivity
import com.nomanim.bunual.base.BaseFragment
import com.nomanim.bunual.extensions.createScaledImageFromBitmap
import com.nomanim.bunual.extensions.generateBitmapFromUri
import com.nomanim.bunual.extensions.loadingProgressBarInDialog
import com.nomanim.bunual.extensions.showCustomBottomSheet
import com.nomanim.bunual.viewmodel.UserViewModel
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import gun0912.tedimagepicker.util.ToastUtil
import gun0912.tedimagepicker.util.ToastUtil.showToast
import java.io.File
import kotlin.collections.ArrayList

class UserFragment : BaseFragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()
    private var sharedPref: SharedPreferences? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private lateinit var loadingDialog: KProgressHUD
    private lateinit var modelAnnouncement: ModelAnnouncement
    private lateinit var placesList: List<ModelPlaces>
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

        loadingDialog = loadingProgressBarInDialog(
            getString(R.string.download), getString(R.string.wait), false
        )
        initUserViewModel()
        initUi()
        mUserViewModel.getPlaces()
    }

    private fun initUserViewModel() {
        mUserViewModel.placesLiveData().observe(viewLifecycleOwner, { response ->
            binding.txtPlaceNames.text =
                sharedPref?.getString("placeName", getString(R.string.choose_place)).toString()
            val placesList = response.sortedWith(compareByDescending { it.population })
            binding.userPlaceCardView.setOnClickListener {
                val listForSheet = ArrayList<String>()
                for (element in placesList) {
                    listForSheet.add(element.city)
                }
                showCustomBottomSheet(
                    listForSheet,
                    binding.txtPlaceNames,
                    getString(R.string.choose_place),
                    true
                )
            }
        })

        mUserViewModel.uploadAdsImagesLiveData().observe(viewLifecycleOwner, { response ->
            val editor = sharedPref?.edit()
            editor?.putString("userName", binding.edtUserName.text.toString())
            editor?.apply()
            getAllDataForUploadToFirestore(response)
            mUserViewModel.uploadAds(firestore, modelAnnouncement)
        })

        mUserViewModel.uploadAdsLiveData().observe(viewLifecycleOwner, { response ->
            if (response == "success") {
                loadingDialog.dismiss()
                val intent = Intent(activity, MainActivity::class.java)
                activity?.finish()
                activity?.startActivity(intent)
                activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        })
        mUserViewModel.errorMutableLiveData.observe(viewLifecycleOwner, { message ->
            binding.txtPlaceNames.text = "error! please try again"
            showToast("error: $message")
        })
    }

    private fun initUi() {
        binding.edtUserName.setText(sharedPref?.getString("userName", "").toString())
        binding.edtUserPhoneNumber.setText(auth.currentUser?.phoneNumber.toString())
        initRadioButton()
        pressBackButton()
        binding.userToolbar.setNavigationOnClickListener {
            navigateToPreviousFragment()
        }
        binding.shareAdsButton.setOnClickListener {
            openGallery()
        }
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

    private fun initRadioButton() {
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

    private fun openGallery() {
        TedImagePicker.with(requireContext())
            .title(R.string.select_phone_image)
            .backButton(R.drawable.back)
            .buttonText("Share")
            .buttonBackground(R.color.main)
            .mediaType(MediaType.IMAGE)
//          .startMultiImage { imagesUri ->
            .start { uri ->

                val fileList = ArrayList<File>()
                val generatedBitmap = activity?.generateBitmapFromUri(uri)
                generatedBitmap?.let { bitmap ->
                    val createdFile = createScaledImageFromBitmap(bitmap, 60)
                    createdFile?.let { file ->
                        fileList.add(file)
                        loadingDialog.show()
                        mUserViewModel.uploadAdsImages(firebaseStorage, fileList)
                    }
                }
            }
    }

    private fun getAllDataForUploadToFirestore(imagesUrl: ArrayList<String>) {
        val brandName = sharedPref?.getString("phoneBrandName", null)
        val modelName = sharedPref?.getString("phoneModelName", null)
        val description = sharedPref?.getString("description", null)
        val storage = sharedPref?.getString("storageCapacity", null)
        val ram = sharedPref?.getString("ramCapacity", null)
        val color = sharedPref?.getString("color", null)
        val status = sharedPref?.getString("status", null)
        val price = sharedPref?.getString("price", null) + "AZN"

        val city = binding.txtPlaceNames.text.toString()
        val userName = binding.edtUserName.text.toString()
        val phoneNumber = binding.edtUserPhoneNumber.text.toString()
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
                imagesUrl,
                description,
                "0",
                Timestamp.now(),
                modelPhone,
                modelUser
            )
        }
    }

    private fun uploadAdsToFirestore() {


    }

    private fun navigateToPreviousFragment() {
        val editor = sharedPref?.edit()
        editor?.putString("userName", binding.edtUserName.text.toString())
        editor?.apply()
        findNavController().navigate(R.id.action_userFragment_to_priceFragment)
    }

}