package com.nomanim.bunual.ui.fragments.mainactivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.AllPhonesAdapter
import com.nomanim.bunual.base.BaseFragment
import com.nomanim.bunual.databinding.FragmentProfileBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.base.responseToList
import com.nomanim.bunual.viewmodel.ProfileViewModel

class ProfileFragment : BaseFragment(), AllPhonesAdapter.Listener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val mProfileViewModel: ProfileViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentUser: FirebaseUser? = null
    private var sharedPref: SharedPreferences? = null

    private var announcements = ArrayList<ModelAnnouncement>()
    private lateinit var userPhoneNumber: String
    private val numberOfAds: Long = 10
    private lateinit var recyclerViewAdapter: AllPhonesAdapter
    private lateinit var lastValue: QuerySnapshot
    private var announcementsAreOver = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.noDataTextView.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser
        sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        if (currentUser != null) {
            userPhoneNumber = currentUser?.phoneNumber.toString()
            binding.phoneNumberTextView.text = userPhoneNumber
            binding.logoutTextView.setOnClickListener {
                showAlertDialog()
            }
            initProfileViewModel()
            mProfileViewModel.getUserAds(firestore, userPhoneNumber, numberOfAds)
        } else {
            findNavController().navigate(R.id.action_profileFragment_to_registrationFragment)
        }
    }

    private fun initProfileViewModel() {
        mProfileViewModel.userAdsLiveData().observe(viewLifecycleOwner, { response ->
            if (response.size() != 0) {
                announcements.responseToList(firestore, userPhoneNumber, response)
                setRecyclerView()
                lastValue = response
                getMoreAds()
            } else {
                binding.noDataTextView.visibility = View.VISIBLE
                binding.currentUserProgressBar.visibility = View.INVISIBLE
            }
        })

        mProfileViewModel.moreLiveData().observe(viewLifecycleOwner, { response ->
            if (response.size() < 10) {
                announcementsAreOver = true
            }
            binding.currentUserProgressBar.visibility = View.INVISIBLE
            lastValue = response
            val morePhones = ArrayList<ModelAnnouncement>()
                .responseToList(
                    firestore,
                    userPhoneNumber,
                    response
                )
            for (i in 0 until morePhones.size) {
                announcements.add(morePhones[i])
            }
            recyclerViewAdapter.notifyDataSetChanged()
        })
        mProfileViewModel.errorMutableLiveData.observe(viewLifecycleOwner, { message ->
            binding.currentUserProgressBar.visibility = View.INVISIBLE
            showToastMessage("error: $message")
        })
    }

    private fun getMoreAds() {
        binding.profilePhonesRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!announcementsAreOver) {
                        binding.currentUserProgressBar.visibility = View.VISIBLE
                        mProfileViewModel.getMoreAds(firestore, lastValue, numberOfAds)
                    }
                }
            }
        })
    }

    private fun setRecyclerView() {
        recyclerViewAdapter = AllPhonesAdapter(
            requireContext(),
            announcements,
            this@ProfileFragment
        ) { model ->
            mMainActivity.intentToAdsDetails(model)
        }
        val recyclerView = binding.profilePhonesRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun showAlertDialog() {
        showDialog(
            title = "Log out",
            message = getString(R.string.are_you_sure),
            positiveButtonText = getString(R.string.log_out),
            negativeButtonText = getString(R.string.dismiss),
            onYesClickListener = { _, _ ->
                auth.signOut()
                findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            }
        )
    }
}