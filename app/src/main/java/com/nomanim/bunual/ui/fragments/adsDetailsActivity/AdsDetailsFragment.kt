package com.nomanim.bunual.ui.fragments.adsDetailsActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.ImagesSliderAdapter
import com.nomanim.bunual.databinding.FragmentAdsDetailsBinding
import com.nomanim.bunual.models.ModelAdsReview
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.ui.activities.MainActivity
import com.nomanim.bunual.adapters.AdsReviewAdapter
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import com.nomanim.bunual.ui.other.getDataFromFireStore
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.thekhaeng.pushdownanim.PushDownAnim


class AdsDetailsFragment : BaseCoroutineScope(), AdsReviewAdapter.Listener {

    private var _binding: FragmentAdsDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var sharedPref: SharedPreferences? = null
    private var imagesLinksAsUri = ArrayList<Uri>()
    private var imagesLinksAsString = ArrayList<String>()
    private var dataOfCurrentAds = ArrayList<ModelAnnouncement>()
    private var currentUserPhoneNumber: String? = null
    private var currentAnnouncementId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAdsDetailsBinding.inflate(inflater)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        binding.allDataLayout.visibility = View.INVISIBLE
        binding.addToFavouritesButton.visibility = View.INVISIBLE
        binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
        binding.adsDetailsBackButton.setOnClickListener { activity?.onBackPressed() }
        currentUserPhoneNumber = auth.currentUser?.phoneNumber.toString()

        getImagesLinksFromHomeFragment()
        showImagesInSlider()
        getDataOfCurrentAds()
        checkAdsIdForFavouritesButtonStatus()

        //BigImageViewer.initialize(FrescoImageLoader.with(requireContext()))
        //BigImageViewer.prefetch(imagesLinksAsUri[0])

        PushDownAnim.setPushDownAnimTo(binding.addToFavouritesButton)
            .setOnClickListener { addAdsToFavourites() }

        PushDownAnim.setPushDownAnimTo(binding.deleteFromFavouritesButton)
            .setOnClickListener { deleteAdsFromFavourites() }

        PushDownAnim.setPushDownAnimTo(binding.callUserPhoneNumberButton)
            .setOnClickListener { openPhoneNumberInPhoneKeyboard() }

    }

    private fun getImagesLinksFromHomeFragment() {

        val imagesLinks = activity?.intent?.getStringExtra("imagesLinks")
        imagesLinksAsString = imagesLinks?.split("|") as ArrayList<String>
        for (i in 0 until imagesLinksAsString.size) {

            imagesLinksAsUri.add(imagesLinksAsString[i].toUri())
        }
    }

    private fun showImagesInSlider() {

        val imagesSlider = binding.imageSlider
        imagesSlider.setSliderAdapter(ImagesSliderAdapter(imagesLinksAsString))
        imagesSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        imagesSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imagesSlider.startAutoCycle()

        /*binding.textView13.setOnClickListener {

            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainer, ShowPicturesFragment())
            fragmentTransaction?.commit()
            binding.nestedScrollView2.visibility = View.INVISIBLE
        }*/
    }

    private fun getDataOfCurrentAds() {

        currentAnnouncementId = activity?.intent?.getStringExtra("selectedAnnouncementId")

        firestore.collection("All Announcements")
            .whereEqualTo("id", currentAnnouncementId)
            .get().addOnSuccessListener { value ->

                dataOfCurrentAds.getDataFromFireStore(firestore, "All Announcements", value)
                setAdsGeneralInformation(dataOfCurrentAds)
                binding.allDataProgressBar.visibility = View.INVISIBLE
                binding.allDataLayout.visibility = View.VISIBLE
                currentAnnouncementId?.let { id -> updateViewOfAdsInFireStore(id) }
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

        firestore.collection(currentUserPhoneNumber!!).addSnapshotListener { value, error ->

            if (value != null) {

                if (value.documents.isNotEmpty()) {

                    checkAdsIdAlgorithm(value)

                } else {

                    binding.addToFavouritesButton.visibility = View.VISIBLE
                    binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
                }
            }

            if (error != null) {

                Toast.makeText(requireContext(), R.string.fail, Toast.LENGTH_SHORT).show()
                binding.addToFavouritesButton.visibility = View.VISIBLE
                binding.deleteFromFavouritesButton.visibility = View.INVISIBLE

            }
        }
    }

    private fun checkAdsIdAlgorithm(value: QuerySnapshot) {

        for (doc in value.documents) {

            val originalAnnouncementId = doc.get("originalAnnouncementId") as String

            if (originalAnnouncementId == currentAnnouncementId) {

                binding.addToFavouritesButton.visibility = View.INVISIBLE
                binding.deleteFromFavouritesButton.visibility = View.VISIBLE

                return

            } else {

                binding.addToFavouritesButton.visibility = View.VISIBLE
                binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun addAdsToFavourites() {

        binding.addToFavouritesButton.visibility = View.INVISIBLE

        if (auth.currentUser != null) {

            if (currentAnnouncementId != null) {
                addAdsIdToCollectionOfUserPhoneNumber()
            } else {
                activity?.onBackPressed()
            }

        } else {

            val intentToProfileFragment = Intent(requireContext(), MainActivity::class.java)
            sharedPref?.edit()?.putBoolean("toProfileFragment", true)?.apply()
            activity?.finish()
            activity?.startActivity(intentToProfileFragment)
        }
    }

    private fun addAdsIdToCollectionOfUserPhoneNumber() {

        val hashMap = HashMap<String, String>()
        hashMap["originalAnnouncementId"] = currentAnnouncementId!!

        firestore.collection(currentUserPhoneNumber!!).document(currentAnnouncementId!!)
            .set(hashMap).addOnSuccessListener {

                checkAdsIdForFavouritesButtonStatus()
            }
    }

    private fun deleteAdsFromFavourites() {

        firestore.collection(currentUserPhoneNumber!!).document(currentAnnouncementId!!).delete()
            .addOnSuccessListener {

                checkAdsIdForFavouritesButtonStatus()
            }
    }

    private fun openPhoneNumberInPhoneKeyboard() {

        val mobileNumber = dataOfCurrentAds[0].user.phoneNumber
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL // Action for what intent called for
        intent.data =
            Uri.parse("tel: $mobileNumber") // Data with intent respective action on intent
        startActivity(intent)
    }

    private fun updateViewOfAdsInFireStore(currentAnnouncementId: String) {

        val newNumberOfViews = (dataOfCurrentAds[0].numberOfViews).toInt() + 1

        firestore.collection("All Announcements").document(currentAnnouncementId)
            .update("numberOfViews",newNumberOfViews.toString())
    }
}