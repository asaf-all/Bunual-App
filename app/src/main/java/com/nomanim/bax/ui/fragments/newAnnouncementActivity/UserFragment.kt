package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.nomanim.bax.R
import com.nomanim.bax.adapters.PlacesAdapter
import com.nomanim.bax.databinding.FragmentUserBinding
import com.nomanim.bax.databinding.LayoutBottomSheetPlacesBinding
import com.nomanim.bax.retrofit.builder.PlacesApi
import com.nomanim.bax.retrofit.models.ModelPlaces
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class UserFragment : Fragment(),PlacesAdapter.Listener {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable = CompositeDisposable()
    private lateinit var bottomSheetBinding: LayoutBottomSheetPlacesBinding
    private lateinit var placesList: List<ModelPlaces>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentUserBinding.inflate(inflater)
        bottomSheetBinding = LayoutBottomSheetPlacesBinding.inflate(inflater)

        getPlacesFromInternet()

        binding.userPlaceCardView.setOnClickListener { setBottomSheet(bottomSheetBinding.root) }

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

            val bottomSheetDialog = BottomSheetDialog(it)
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


    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}