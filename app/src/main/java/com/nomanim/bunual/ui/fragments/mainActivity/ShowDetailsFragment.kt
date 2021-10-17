package com.nomanim.bunual.ui.fragments.mainActivity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.adapters.ImagesSliderAdapter
import com.nomanim.bunual.ui.adapters.SimilarPhonesAdapter
import com.nomanim.bunual.databinding.FragmentShowDetailsBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import com.nomanim.bunual.ui.other.getDataFromFireStore
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations


class ShowDetailsFragment : BaseCoroutineScope(), SimilarPhonesAdapter.Listener {

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private var imagesLinksAsUri = ArrayList<Uri>()
    private var imagesLinksAsString = ArrayList<String>()
    private val similarPhones = ArrayList<ModelAnnouncement>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentShowDetailsBinding.inflate(inflater)
        firestore = FirebaseFirestore.getInstance()

        getImagesLinkFromSharedPref()
        pressBackButtonInImagesSlider()

        BigImageViewer.initialize(FrescoImageLoader.with(requireContext()))
        BigImageViewer.prefetch(imagesLinksAsUri[0])
        Log.e("********",imagesLinksAsUri.toString())

        setImagesInSlider()
        getSimilarPhonesFromFireStore()

        return binding.root
    }

    private fun getImagesLinkFromSharedPref() {

        val sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        val imagesLinksStringSet = sharedPref?.getString("imagesLinks",null)
        imagesLinksAsString = imagesLinksStringSet?.split("|") as ArrayList<String>
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
            fragmentTransaction?.replace(R.id.fragmentContainer,ShowPicturesFragment())
            fragmentTransaction?.commit()
            binding.nestedScrollView2.visibility = View.INVISIBLE
        }
    }

    private fun getSimilarPhonesFromFireStore() {

        firestore.collection("All Announcements").get().addOnSuccessListener { value ->

            similarPhones.getDataFromFireStore(firestore,"All Announcements",value)
            setSimilarPhonesRecyclerView()
        }
    }

    private fun setSimilarPhonesRecyclerView() {

        context?.let {

            val recyclerView = binding.similarPhonesRecyclerView
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL)
            val adapter = SimilarPhonesAdapter(it,similarPhones,this@ShowDetailsFragment)
            recyclerView.adapter = adapter
        }
    }

    private fun pressBackButtonInImagesSlider() {

        binding.showDetailsBackButton.setOnClickListener {

            activity?.findViewById<FrameLayout>(R.id.detailsFragmentContainer)?.visibility = View.GONE
            activity?.findViewById<NestedScrollView>(R.id.nestedScrollView)?.visibility = View.VISIBLE
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.VISIBLE }
    }

    override fun onSimilarPhoneClick() { }



}