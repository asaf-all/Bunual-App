package com.nomanim.bunual.ui.fragments.adsDetailsActivity

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.adapters.ImagesSliderAdapter
import com.nomanim.bunual.databinding.FragmentAdsDetailsBinding
import com.nomanim.bunual.models.ModelAdsReview
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.ui.adapters.AdsReviewAdapter
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import com.nomanim.bunual.ui.other.getDataFromFireStore
import com.nomanim.bunual.ui.other.ktx.addCurrentUserPhoneNumberToSharedPref
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentAdsDetailsBinding.inflate(inflater)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)

        binding.allDataLayout.visibility = View.INVISIBLE
        binding.addToFavouritesButton.visibility = View.INVISIBLE
        binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
        binding.adsDetailsBackButton.setOnClickListener { activity?.onBackPressed() }

        getImagesLinkFromHomeFragment()
        setImagesInSlider()
        getDataOfCurrentAds()
        checkAdsIdForFavouritesButtonStatus()

        BigImageViewer.initialize(FrescoImageLoader.with(requireContext()))
        BigImageViewer.prefetch(imagesLinksAsUri[0])

        PushDownAnim.setPushDownAnimTo(binding.addToFavouritesButton)
            .setOnClickListener {

                binding.addToFavouritesButton.visibility = View.INVISIBLE
                addAdsToFavourites() }

        return binding.root
    }

    private fun getImagesLinkFromHomeFragment() {

        val imagesLinks = activity?.intent?.getStringExtra("imagesLinks")
        imagesLinksAsString = imagesLinks?.split("|") as ArrayList<String>
        for (i in 0 until imagesLinksAsString.size) { imagesLinksAsUri.add(imagesLinksAsString[i].toUri()) }
    }

    private fun setImagesInSlider() {

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
            .whereEqualTo("id",currentAnnouncementId)
            .get().addOnSuccessListener { value ->

                dataOfCurrentAds.getDataFromFireStore(firestore,"All Announcements",value)
                setAdsGeneralInformation()
                binding.allDataProgressBar.visibility = View.INVISIBLE
                binding.allDataLayout.visibility = View.VISIBLE
        }
    }

    private fun setAdsGeneralInformation() {

        binding.adsPrice.text = dataOfCurrentAds[0].phone.price
        binding.adsBrandName.text = dataOfCurrentAds[0].phone.brand
        binding.adsModelName.text = dataOfCurrentAds[0].phone.model
        binding.adsDescription.text = dataOfCurrentAds[0].description

        setAdsOtherInformation()
    }

    private fun setAdsOtherInformation() {

        val storage = ModelAdsReview(getString(R.string.storage), dataOfCurrentAds[0].phone.storage)
        val ram = ModelAdsReview(getString(R.string.ram), dataOfCurrentAds[0].phone.ram)
        val color = ModelAdsReview(getString(R.string.color), dataOfCurrentAds[0].phone.color)
        val status = ModelAdsReview(getString(R.string.status), dataOfCurrentAds[0].phone.currentStatus)
        val delivery = ModelAdsReview(getString(R.string.delivery), dataOfCurrentAds[0].phone.delivery)

        val list = ArrayList<ModelAdsReview>()
        list.add(storage)
        list.add(ram)
        list.add(color)
        list.add(status)
        list.add(delivery)

        setAdsReviewRecyclerView(list)
    }

    private fun setAdsReviewRecyclerView(list: ArrayList<ModelAdsReview>) {

        val rv = binding.adsReviewRecyclerView
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(context)
        rv.isNestedScrollingEnabled = false
        rv.adapter = AdsReviewAdapter(list,this@AdsDetailsFragment)
    }

    private fun checkAdsIdForFavouritesButtonStatus() {

        currentUserPhoneNumber = sharedPref?.getString("userPhoneNumber",null)

        if (currentUserPhoneNumber == null) {

            addCurrentUserPhoneNumberToSharedPref(auth, sharedPref)
            checkAdsIdForFavouritesButtonStatus()

        }else {

            firestore.collection(currentUserPhoneNumber!!).addSnapshotListener() { value,error ->

                if (value != null) {

                    if (value.documents.isNotEmpty()) {

                        checkAdsIdAlgorithm(value)

                    }else {

                        binding.addToFavouritesButton.visibility = View.VISIBLE
                        binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
                    }
                }

                if (error != null) {

                    Toast.makeText(requireContext(),R.string.fail,Toast.LENGTH_SHORT).show()
                    binding.addToFavouritesButton.visibility = View.VISIBLE
                    binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun checkAdsIdAlgorithm(value: QuerySnapshot) {

        for(doc in value.documents) {

            Log.e("*********",doc.get("originalAnnouncementId") as String)

            val originalAnnouncementId = doc.get("originalAnnouncementId") as String

            if (originalAnnouncementId == currentAnnouncementId) {

                binding.addToFavouritesButton.visibility = View.INVISIBLE
                binding.deleteFromFavouritesButton.visibility = View.VISIBLE

            }else {

                binding.addToFavouritesButton.visibility = View.VISIBLE
                binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun addAdsToFavourites() {

        if (currentUserPhoneNumber == null) {

            addCurrentUserPhoneNumberToSharedPref(auth, sharedPref)
            addAdsToFavourites()

        }else {

            if (currentAnnouncementId != null) {

                addAdsIdToCollectionOfUserPhoneNumber()
            }
        }
    }

    private fun addAdsIdToCollectionOfUserPhoneNumber() {

        val hashMap = HashMap<String,String>()
        hashMap["originalAnnouncementId"] = currentAnnouncementId!!

        firestore.collection(currentUserPhoneNumber!!).add(hashMap).addOnSuccessListener {

            checkAdsIdForFavouritesButtonStatus()
        }
    }


}