package com.nomanim.bunual.ui.fragments.adsdetailsactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.nomanim.bunual.Constants
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.ImagesSliderAdapter
import com.nomanim.bunual.databinding.FragmentAdsDetailsBinding
import com.nomanim.bunual.models.ModelAdsReview
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.adapters.AdsReviewAdapter
import com.nomanim.bunual.base.BaseCoroutineScope
import com.nomanim.bunual.base.responseToItem
import com.nomanim.bunual.viewmodel.AdsDetailsViewModel
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations


class AdsDetailsFragment : BaseCoroutineScope(), AdsReviewAdapter.Listener {

    private var _binding: FragmentAdsDetailsBinding? = null
    private val binding get() = _binding!!
    private val mDetailsViewModel: AdsDetailsViewModel by viewModels()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var announcement: ModelAnnouncement? = null
    private var sharedPref: SharedPreferences? = null
    private var dataOfCurrentAds = ArrayList<ModelAnnouncement>()
    private var userPhoneNumber: String? = null
    private var currentAdsId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdsDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        announcement = activity?.intent?.getParcelableExtra("announcement")

        binding.allDataLayout.visibility = View.INVISIBLE

        initDetailsViewModel()
        makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(binding.detailContainerLayout) { _, insets ->
            binding.adsDetailsBackButton.setMarginTop((insets.systemWindowInsetTop) + 20)
            insets.consumeSystemWindowInsets()
        }

        userPhoneNumber = auth.currentUser?.phoneNumber.toString()
        currentAdsId = announcement?.id
        currentAdsId?.let { id ->
            mDetailsViewModel.getCurrentAds(firestore, id)
        }
    }

    private fun makeStatusBarTransparent() {
        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun View.setMarginTop(marginTop: Int) {
        val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        menuLayoutParams.setMargins(0, marginTop, 0, 0)
        this.layoutParams = menuLayoutParams
    }

    private fun initDetailsViewModel() {
        mDetailsViewModel.currentAdsLiveData().observe(viewLifecycleOwner, { response ->
            dataOfCurrentAds.responseToItem(firestore, Constants.ADS_COLLECTION_NAME, response)
            setAdsGeneralInformation(dataOfCurrentAds)
            binding.allDataProgressBar.visibility = View.INVISIBLE
            binding.allDataLayout.visibility = View.VISIBLE
            currentAdsId?.let { id ->
                updateViewOfAds(id)
            }
            initUi(announcement)
        })
        mDetailsViewModel.addToFavoritesLiveData().observe(viewLifecycleOwner, { response ->
            if (response == "success") {
                checkAdsIdForFavouritesButtonStatus()
            }
        })
        mDetailsViewModel.deleteFromFavoritesLiveData().observe(viewLifecycleOwner, { response ->
            if (response == "success") {
                checkAdsIdForFavouritesButtonStatus()
            }
        })
        mDetailsViewModel.errorMutableLiveData.observe(viewLifecycleOwner, { message ->
            binding.allDataProgressBar.visibility = View.INVISIBLE
            showToastMessage("error: $message")
        })
    }

    private fun initUi(announcement: ModelAnnouncement?) {
        showImagesInSlider(announcement?.image)
        checkAdsIdForFavouritesButtonStatus()

        binding.adsDetailsBackButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.imageView7.setOnClickListener {
            val intent = Intent(context, GalleryImageActivity::class.java)
            intent.putExtra("photos", Gson().toJson(dataOfCurrentAds[0].image))
            intent.putExtra("position", Gson().toJson(dataOfCurrentAds.map { it.image }))
            context?.startActivity(intent)
        }
        binding.btnAddToFav.setOnClickListener {
            binding.btnAddToFav.visibility = View.INVISIBLE
            if (auth.currentUser != null) {
                if (userPhoneNumber != null && currentAdsId != null) {
                    mDetailsViewModel.addToFavorites(firestore, userPhoneNumber!!, currentAdsId!!)
                }
            } else {
                showToastMessage("You must create an account.", lengthIsLong = true)
            }
        }
        binding.btnCall.setOnClickListener {
            openPhoneNumberInAppOfCall()
        }
        binding.btnDeleteFromFav.setOnClickListener {
            if (userPhoneNumber != null && currentAdsId != null) {
                mDetailsViewModel.deleteFromFavorites(
                    firestore,
                    userPhoneNumber!!,
                    currentAdsId!!
                )
            }
        }
    }

    private fun showImagesInSlider(imagesUrl: ArrayList<String>?) {
        val slider = binding.imageSlider
        if (imagesUrl != null) {
            slider.setSliderAdapter(ImagesSliderAdapter(imagesUrl))
            slider.setIndicatorAnimation(IndicatorAnimationType.WORM)
            slider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            slider.startAutoCycle()
        }
    }

    private fun setAdsGeneralInformation(list: ArrayList<ModelAnnouncement>) {
        binding.adsPrice.text = list[0].phone.price
        binding.adsBrandName.text = list[0].phone.brand
        binding.adsModelName.text = list[0].phone.model
        binding.adsDescription.text = list[0].description
        binding.announcementViews2.text = list[0].numberOfViews
        setAdsOtherInformation(list)
    }

    private fun setAdsOtherInformation(list: ArrayList<ModelAnnouncement>) {
        val storage = ModelAdsReview(getString(R.string.storage), list[0].phone.storage)
        val ram = ModelAdsReview(getString(R.string.ram), list[0].phone.ram)
        val color = ModelAdsReview(getString(R.string.color), list[0].phone.color)
        val status = ModelAdsReview(getString(R.string.status), list[0].phone.currentStatus)
        val delivery = ModelAdsReview(getString(R.string.delivery), list[0].phone.delivery)
        val place = ModelAdsReview(getString(R.string.user_place), list[0].user.places.city)
        val userName = ModelAdsReview(getString(R.string.user_name), list[0].user.name)
        val userPhoneNumber =
            ModelAdsReview(getString(R.string.user_phone_number), list[0].user.phoneNumber)
        val date = ModelAdsReview(
            getString(R.string.date),
            android.text.format.DateFormat.format("dd.MM.yyyy", list[0].time.toDate()).toString()
        )

        val reviewName = ArrayList<ModelAdsReview>()
        reviewName.add(storage)
        reviewName.add(ram)
        reviewName.add(color)
        reviewName.add(status)
        reviewName.add(delivery)

        val userData = ArrayList<ModelAdsReview>()
        userData.add(place)
        userData.add(userName)
        userData.add(userPhoneNumber)
        userData.add(date)

        setAdsReviewRecyclerView(reviewName)
        setDataOfHostUserOfAds(userData)
    }

    private fun setAdsReviewRecyclerView(reviewName: ArrayList<ModelAdsReview>) {
        val rv = binding.adsReviewRecyclerView
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(context)
        rv.isNestedScrollingEnabled = false
        rv.adapter = AdsReviewAdapter(reviewName, this@AdsDetailsFragment)
    }

    private fun setDataOfHostUserOfAds(userData: ArrayList<ModelAdsReview>) {
        val rv = binding.hostUserDataRecyclerView
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(context)
        rv.isNestedScrollingEnabled = false
        rv.adapter = AdsReviewAdapter(userData, this@AdsDetailsFragment)
    }

    private fun checkAdsIdForFavouritesButtonStatus() {
        firestore.collection(userPhoneNumber!!).addSnapshotListener { value, error ->
            if (value != null) {
                if (value.documents.isNotEmpty()) {
                    checkAdsIdAlgorithm(value)
                } else {
                    binding.btnAddToFav.visibility = View.VISIBLE
                    binding.btnDeleteFromFav.visibility = View.INVISIBLE
                }
            }
            if (error != null) {
                Toast.makeText(requireContext(), R.string.fail, Toast.LENGTH_SHORT).show()
                binding.btnAddToFav.visibility = View.VISIBLE
                binding.btnDeleteFromFav.visibility = View.INVISIBLE

            }
        }
    }

    private fun checkAdsIdAlgorithm(value: QuerySnapshot) {
        for (doc in value.documents) {
            val originalAdsId = doc.get("originalAdsId") as String
            if (originalAdsId == currentAdsId) {
                binding.btnAddToFav.visibility = View.INVISIBLE
                binding.btnDeleteFromFav.visibility = View.VISIBLE
                return
            } else {
                binding.btnAddToFav.visibility = View.VISIBLE
                binding.btnDeleteFromFav.visibility = View.INVISIBLE
            }
        }
    }

    private fun openPhoneNumberInAppOfCall() {
        val mobileNumber = dataOfCurrentAds[0].user.phoneNumber
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        intent.data = Uri.parse("tel: $mobileNumber")
        startActivity(intent)
    }

    private fun updateViewOfAds(currentAnnouncementId: String) {
        val newNumberOfViews = (dataOfCurrentAds[0].numberOfViews).toInt() + 1
        firestore.collection(Constants.ADS_COLLECTION_NAME).document(currentAnnouncementId)
            .update("numberOfViews", newNumberOfViews.toString())
    }
}