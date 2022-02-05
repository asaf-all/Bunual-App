package com.nomanim.bunual.ui.fragments.newadsactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.nomanim.bunual.R
import com.nomanim.bunual.base.*
import com.nomanim.bunual.databinding.FragmentUserBinding
import com.nomanim.bunual.ui.activities.MainActivity
import com.nomanim.bunual.extensions.createScaledImageFromBitmap
import com.nomanim.bunual.extensions.generateBitmapFromUri
import com.nomanim.bunual.extensions.loadingProgressBarInDialog
import com.nomanim.bunual.extensions.showCustomBottomSheet
import com.nomanim.bunual.viewmodel.UserViewModel
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import gun0912.tedimagepicker.util.ToastUtil.showToast
import java.io.File
import kotlin.collections.ArrayList

class UserFragment : BaseFragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UserFragmentArgs>()
    private val mUserViewModel: UserViewModel by viewModels()

    private var sharedPref: SharedPreferences? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private lateinit var loadingDialog: KProgressHUD
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
        sharedPref = activity?.getSharedPreferences("newAdsActivity", Context.MODE_PRIVATE)
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        loadingDialog = loadingProgressBarInDialog(
            getString(R.string.download), getString(R.string.wait), false
        )
        initUserViewModel()
        initUi()
        mUserViewModel.getPlaces()
    }

    private fun initUserViewModel() {
        mUserViewModel.placesLiveData().observe(viewLifecycleOwner) { response ->
            binding.txtPlaceNames.text =
                sharedPref?.getString("placeName", getString(R.string.choose_place)).toString()

            binding.userPlaceCardView.setOnClickListener {
                val regionsList = response.sortedByDescending { regions ->
                    regions.population
                }
                val citiesList = mutableListOf<String>()
                for (element in regionsList) {
                    citiesList.add(element.city)
                }

                val uniqueCities = mutableListOf<String>()
                val iterator = citiesList.iterator() // for remove recurring city names
                while (iterator.hasNext()) {
                    val element = iterator.next()
                    if (!uniqueCities.contains(element)) {
                        uniqueCities.add(element)
                    }
                }
                showCustomBottomSheet(
                    uniqueCities,
                    binding.txtPlaceNames,
                    getString(R.string.choose_place),
                    true
                )
            }
        }

        mUserViewModel.uploadAdsImagesLiveData().observe(viewLifecycleOwner) { response ->
            val editor = sharedPref?.edit()
            editor?.putString("userName", binding.edtUserName.text.toString())
            editor?.apply()
            uploadAdsToFirestore(response)
        }

        mUserViewModel.uploadAdsLiveData().observe(viewLifecycleOwner) { response ->
            if (response == "success") {
                loadingDialog.dismiss()
                val intent = Intent(activity, MainActivity::class.java)
                activity?.finish()
                activity?.startActivity(intent)
                activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
        mUserViewModel.errorMutableLiveData.observe(viewLifecycleOwner) { message ->
            binding.txtPlaceNames.text = "error! please try again"
            showToast("error: $message")
        }
    }


    private fun initUi() {
        binding.edtUserName.setText(
            sharedPref?.getString("userName", "").toString()
        )
        binding.edtUserPhoneNumber.setText(auth.currentUser?.phoneNumber.toString())
        initRadioButton()
        pressBackButton()
        binding.userToolbar.setNavigationOnClickListener {
            navigateToPreviousFragment()
        }
        binding.shareAdsButton.setOnClickListener {
            if (auth.currentUser != null) {
                openGallery()
            } else {
                showDialog(
                    "You don't have permission!",
                    "Currently you are in discover mode. " +
                            "If you want to share announcement you must create account.",
                    "OK"
                )
            }
        }
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

    private fun uploadAdsToFirestore(imagesUrl: ArrayList<String>) {
        val city = binding.txtPlaceNames.text.toString()
        val userName = binding.edtUserName.text.toString()
        val phoneNumber = binding.edtUserPhoneNumber.text.toString()

        if (TextUtils.isEmpty(city) &&
            TextUtils.isEmpty(userName) &&
            TextUtils.isEmpty(phoneNumber) &&
            deliveryStatus == ""
        ) {
            Snackbar.make(
                binding.root,
                resources.getString(R.string.fill_in_all),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {

            val places = args.announcement.user.places.copy(city = city)
            val user = args.announcement.user.copy(
                name = userName,
                phoneNumber = phoneNumber,
                places = places
            )
            val phone = args.announcement.phone.copy(delivery = deliveryStatus)
            val announcement = args.announcement.copy(
                user_token = auth.currentUser?.phoneNumber.toString(),
                time = Timestamp.now(),
                user = user,
                phone = phone,
                image = imagesUrl
            )
            mUserViewModel.uploadAds(firestore, announcement)
        }
    }

    private fun navigateToPreviousFragment() {
        val editor = sharedPref?.edit()
        editor?.putString("userName", binding.edtUserName.text.toString())
        editor?.apply()
        findNavController().navigate(
            UserFragmentDirections.actionUserFragmentToPriceFragment(args.announcement)
        )
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
}