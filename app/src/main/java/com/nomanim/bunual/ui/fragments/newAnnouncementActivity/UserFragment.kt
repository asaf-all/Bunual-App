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
import com.nomanim.bunual.databinding.LayoutBottomSheetPlacesBinding
import com.nomanim.bunual.retrofit.builders.PlacesApi
import com.nomanim.bunual.retrofit.models.ModelPlaces
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class UserFragment : Fragment(),PlacesAdapter.Listener {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable = CompositeDisposable()
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var bottomSheetBinding: LayoutBottomSheetPlacesBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var placesList: List<ModelPlaces>
    private val downloadUrlList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentUserBinding.inflate(inflater)
        bottomSheetBinding = LayoutBottomSheetPlacesBinding.inflate(inflater)

        pressBackButton()
        getPlacesFromInternet()

        binding.userPlaceCardView.setOnClickListener { setBottomSheet(bottomSheetBinding.root) }
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
                    setPlacesRecyclerView()
                }

                override fun onError(error: Throwable) {

                    Snackbar.make(binding.root,getString(R.string.fail),Snackbar.LENGTH_SHORT).show()
                    error.printStackTrace()
                }
            }))
    }

    private fun setBottomSheet(bottomSheetView:View) {

        if (bottomSheetView.parent != null) {

            (bottomSheetView.parent as ViewGroup).removeAllViews()
        }
        context?.let {

            bottomSheetDialog = BottomSheetDialog(it)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }

    private fun setPlacesRecyclerView() {

        val recyclerView = bottomSheetBinding.placesRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        context?.let {

            recyclerView.layoutManager = LinearLayoutManager(it)
            val adapter = PlacesAdapter(placesList,this@UserFragment)
            recyclerView.adapter = adapter
        }
    }

    override fun setOnPlaceClickListener(buttonFinishText: String) {

        bottomSheetDialog.dismiss()
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