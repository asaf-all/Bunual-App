package com.nomanim.bunual.ui.fragments.mainactivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.Constants
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.MostViewedPhonesAdapter
import com.nomanim.bunual.adapters.AllPhonesAdapter
import com.nomanim.bunual.databinding.FragmentHomeBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.base.BaseFragment
import com.nomanim.bunual.base.responseToList
import com.nomanim.bunual.viewmodel.HomeViewModel
import com.thekhaeng.pushdownanim.PushDownAnim
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment(), MostViewedPhonesAdapter.Listener,
    AllPhonesAdapter.Listener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mHomeViewModel: HomeViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var sharedPref: SharedPreferences? = null

    private val sortTexts = ArrayList<String>()
    private var mostViewedPhones = ArrayList<ModelAnnouncement>()
    private var allPhones = ArrayList<ModelAnnouncement>()
    private lateinit var verticalRVAdapter: AllPhonesAdapter
    private var userPhoneNumber: String = ""
    private val numberOfAds = 10L  //for load data limit from fireStore for once
    private lateinit var lastValue: QuerySnapshot
    private var adsAreOver = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        userPhoneNumber = auth.currentUser?.phoneNumber.toString()
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.background_color_gray)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility =
            View.VISIBLE

        initHomeViewModel()
        mHomeViewModel.getMostViewedAds(firestore, numberOfAds)
        mHomeViewModel.getAllAds(firestore, numberOfAds)
        initUi()
    }

    private fun initHomeViewModel() {
        mHomeViewModel.mostViewedLiveData().observe(viewLifecycleOwner, { response ->
            mostViewedPhones.responseToList(firestore, value = response)
            setHorizontalRecyclerView()
        })
        mHomeViewModel.allLiveData().observe(viewLifecycleOwner, { response ->
            allPhones.responseToList(firestore, Constants.ADS_COLLECTION_NAME, response)
            setVerticalRecyclerView()

            if (response.size() != 0) {
                lastValue = response
                getMorePhonesFromFireStore()
            }
        })
        mHomeViewModel.moreLiveData().observe(viewLifecycleOwner, { response ->
            if (response.size() < numberOfAds) {
                adsAreOver = true
            }
            binding.morePhonesProgressBar.visibility = View.INVISIBLE
            lastValue = response
            val morePhones = ArrayList<ModelAnnouncement>()
                .responseToList(firestore, Constants.ADS_COLLECTION_NAME, response)
            for (i in 0 until morePhones.size) {
                allPhones.add(morePhones[i])
            }
            verticalRVAdapter.notifyDataSetChanged()
        })
        mHomeViewModel.errorMutableLiveData.observe(viewLifecycleOwner, { message ->
            showToastMessage("error: $message")
        })
    }

    private fun getMorePhonesFromFireStore() {
        val scrollView = binding.nestedScrollView
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.getChildAt(0).bottom <= (scrollView.height + scrollView.scrollY)) {
                if (!adsAreOver) {
                    binding.morePhonesProgressBar.visibility = View.VISIBLE
                    mHomeViewModel.getMoreAds(firestore, lastValue, numberOfAds)
                }
            }
        }
    }

    private fun setHorizontalRecyclerView() {
        val adapter = MostViewedPhonesAdapter(mostViewedPhones, this@HomeFragment) { model ->
            mMainActivity.intentToAdsDetails(model)
        }
        val hrv = binding.horizontalRecyclerView
        hrv.setHasFixedSize(true)
        hrv.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        hrv.adapter = adapter
    }

    private fun setVerticalRecyclerView() {
        verticalRVAdapter = AllPhonesAdapter(requireContext(), allPhones, this@HomeFragment) { model ->
            mMainActivity.intentToAdsDetails(model)
        }
        val vrv = binding.verticalRecyclerView
        vrv.isNestedScrollingEnabled = false
        vrv.setHasFixedSize(true)
        vrv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        vrv.adapter = verticalRVAdapter
    }

    private fun initUi() {
        PushDownAnim.setPushDownAnimTo(binding.cardView2).setOnClickListener {
            Snackbar.make(binding.root, "will be activate", Snackbar.LENGTH_SHORT).show()
            for (text in resources.getStringArray(R.array.sort_data_texts)) {
                sortTexts.add(text)
            }
        }
    }

//  private fun createListWithSelectedAdsImages(
//      list: ArrayList<ModelAnnouncement>,
//      position: Int
//  ): StringBuilder {
//      val stringBuilder = StringBuilder()
//      for (i in 0 until list[position].image.size) {
//          if (i == list[position].image.size - 1) {
//              stringBuilder.append(list[position].image[i])
//          } else {
//              stringBuilder.append(list[position].image[i] + "|")
//          }
//      }
//      return stringBuilder
//  }
}