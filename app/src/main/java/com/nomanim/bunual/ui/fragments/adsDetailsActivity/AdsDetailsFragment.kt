package com.nomanim.bunual.ui.fragments.adsDetailsActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.adapters.ImagesSliderAdapter
import com.nomanim.bunual.databinding.FragmentAdsDetailsBinding
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.coroutines.launch


class AdsDetailsFragment : BaseCoroutineScope() {

    private var _binding: FragmentAdsDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var sharedPref: SharedPreferences? = null
    private var imagesLinksAsUri = ArrayList<Uri>()
    private var imagesLinksAsString = ArrayList<String>()
    private var currentUserPhoneNumber: String? = null
    private var currentAnnouncementId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentAdsDetailsBinding.inflate(inflater)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        currentUserPhoneNumber = sharedPref?.getString("userPhoneNumber",null)

        binding.addToFavouritesButton.visibility = View.INVISIBLE
        binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
        binding.adsDetailsBackButton.setOnClickListener { activity?.onBackPressed() }

        getImagesLinkFromHomeFragment()

        lifecycleScope.launch { checkAdsIdForFavouritesButtonStatus() }

        BigImageViewer.initialize(FrescoImageLoader.with(requireContext()))
        BigImageViewer.prefetch(imagesLinksAsUri[0])

        PushDownAnim.setPushDownAnimTo(binding.addToFavouritesButton)
            .setOnClickListener { addAdsToFavourites() }

        setImagesInSlider()

        return binding.root
    }

    private fun getImagesLinkFromHomeFragment() {

        val imagesLinks= activity?.intent?.getStringExtra("imagesLinks")
        imagesLinksAsString = imagesLinks?.split("|") as ArrayList<String>
        for (i in 0 until imagesLinksAsString.size) { imagesLinksAsUri.add(imagesLinksAsString[i].toUri()) }
    }

    private fun setImagesInSlider() {

        val imagesSlider = binding.imageSlider
        imagesSlider.setSliderAdapter(ImagesSliderAdapter(imagesLinksAsString))
        imagesSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        imagesSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imagesSlider.startAutoCycle()

        binding.textView13.setOnClickListener {

            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainer, ShowPicturesFragment())
            fragmentTransaction?.commit()
            binding.nestedScrollView2.visibility = View.INVISIBLE
        }
    }

    private fun checkAdsIdForFavouritesButtonStatus() {

        if (currentUserPhoneNumber == null) {

            addCurrentUserPhoneNumberToSharedPref()
            checkAdsIdForFavouritesButtonStatus()

        }else {

            firestore.collection(currentUserPhoneNumber!!).get().addOnSuccessListener { value ->

                if (value != null) {

                    if (value.documents.isNotEmpty()) {

                        checkAdsIdAlgorithm(value)

                    }else {

                        binding.addToFavouritesButton.isEnabled = true
                        binding.addToFavouritesButton.visibility = View.VISIBLE
                        binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun checkAdsIdAlgorithm(value: QuerySnapshot) {

        for(doc in value.documents) {

            if (doc.get("originalAnnouncementId") as String == currentAnnouncementId) {

                binding.addToFavouritesButton.visibility = View.INVISIBLE
                binding.deleteFromFavouritesButton.visibility = View.VISIBLE

            }else {

                binding.addToFavouritesButton.isEnabled = true
                binding.addToFavouritesButton.visibility = View.VISIBLE
                binding.deleteFromFavouritesButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun addAdsToFavourites() {

        if (currentUserPhoneNumber == null) {

            addCurrentUserPhoneNumberToSharedPref()
            addAdsToFavourites()

        }else {

            if (currentAnnouncementId != null) {

                addAdsIdToCollectionOfUserPhoneNumber(currentUserPhoneNumber!!,currentAnnouncementId!!)
            }
        }
    }

    private fun addCurrentUserPhoneNumberToSharedPref() {

        val currentPhoneNumber = auth.currentUser?.phoneNumber.toString()
        val editor = sharedPref?.edit()
        editor?.putString("userPhoneNumber",currentPhoneNumber)
        editor?.apply()
    }

    private fun addAdsIdToCollectionOfUserPhoneNumber(currentUserPhoneNumber: String,currentAnnouncementId: String) {

        val hashMap = HashMap<String,String>()
        hashMap["originalAnnouncementId"] = currentAnnouncementId

        firestore.collection(currentUserPhoneNumber).add(hashMap).addOnSuccessListener {

            binding.addToFavouritesButton.isEnabled = false
            checkAdsIdForFavouritesButtonStatus()
        }
    }

}