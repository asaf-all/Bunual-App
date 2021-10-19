package com.nomanim.bunual.ui.fragments.newAnnouncementActivity

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.adapters.PlacesAdapter
import com.nomanim.bunual.databinding.FragmentUserBinding
import com.nomanim.bunual.databinding.LayoutBottomSheetColorsBinding
import com.nomanim.bunual.retrofit.builders.PlacesApi
import com.nomanim.bunual.retrofit.models.ModelPlaces
import com.nomanim.bunual.ui.other.ktx.showFeaturesBottomSheet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable = CompositeDisposable()
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var placesList: List<ModelPlaces>
    private val downloadUrlList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentUserBinding.inflate(inflater)

        pressBackButton()
        getPlacesFromInternet()

        binding.userToolbar.setNavigationOnClickListener { navigateToPreviousFragment() }

        return binding.root
    }

    private fun getPlacesFromInternet() {

        compositeDisposable.add(PlacesApi.builder.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<ModelPlaces>>() {

                override fun onSuccess(list: List<ModelPlaces>) {

                    placesList = list.sortedWith(compareByDescending { it.population })
                    binding.userPlaceCardView.setOnClickListener {

                        val listForDialog = ArrayList<String>()
                        for (i in 0 until placesList.size) { listForDialog.add(placesList[i].city) }

                        showFeaturesBottomSheet(listForDialog,binding.placeTextView,getString(R.string.choose_place)) }
                }

                override fun onError(error: Throwable) {

                    Snackbar.make(binding.root,getString(R.string.fail),Snackbar.LENGTH_SHORT).show()
                    error.printStackTrace()
                }
            }))
    }


    private fun uploadImagesToStorage(imagesList: List<Uri>) {

        val reference = firebaseStorage.reference

        val child = reference.child("Pictures").child(UUID.randomUUID().toString())
        child.putFile(imagesList[0]).addOnSuccessListener {

            downloadUrlList.add(child.downloadUrl.toString())
            //navigateToNextFragment()
        }

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

        findNavController().navigate(R.id.action_userFragment_to_priceFragment)
    }

    private fun pressBackButton() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                findNavController().navigate(R.id.action_userFragment_to_priceFragment)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}