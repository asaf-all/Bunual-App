package com.nomanim.bax.ui.fragments.mainActivity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bax.R
import com.nomanim.bax.adapters.MostViewedPhonesAdapter
import com.nomanim.bax.adapters.AllPhonesAdapter
import com.nomanim.bax.databinding.FragmentHomeBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.ui.other.getDataFromFireStore
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(),MostViewedPhonesAdapter.Listener,AllPhonesAdapter.Listener{

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val sortTexts = ArrayList<String>()
    private var mostViewedPhones = ArrayList<ModelAnnouncement>()
    private var allPhones = ArrayList<ModelAnnouncement>()
    private lateinit var verticalRecyclerViewAdapter: AllPhonesAdapter

    private var currentUserPhoneNumber: String = ""
    private val numberOfAnnouncement = 10L  //for load data limit from fireStore for once
    private lateinit var lastValue: QuerySnapshot
    private var announcementsAreOver = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserPhoneNumber = auth.currentUser?.phoneNumber.toString()

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.VISIBLE

        getMostViewedPhonesFromFireStore()
        getAllPhonesFromFireStore()

        return binding.root
    }

    private fun addSortTextsToList() {

        for (text in resources.getStringArray(R.array.sort_data_texts) ) {
            sortTexts.add(text)
        }
    }

    private fun getMostViewedPhonesFromFireStore() {

        firestore.collection("All Announcements")
            .orderBy("numberOfViews", Query.Direction.DESCENDING).limit(numberOfAnnouncement).get().addOnSuccessListener { values ->

                binding.mostViewedProgressBar.visibility = View.INVISIBLE
                mostViewedPhones.getDataFromFireStore(firestore,"All Announcements",values)
                setHorizontalRecyclerView()

        }.addOnFailureListener { exception ->

                binding.mostViewedProgressBar.visibility = View.INVISIBLE
                context?.let { Toast.makeText(it,exception.localizedMessage?.toString(),Toast.LENGTH_SHORT).show() }
        }
    }

    private fun getAllPhonesFromFireStore() {

        firestore.collection("All Announcements")
            .limit(numberOfAnnouncement).orderBy("time",Query.Direction.ASCENDING).get().addOnSuccessListener { values ->

            binding.allPhonesProgressBar.visibility = View.INVISIBLE
            allPhones.getDataFromFireStore(firestore,"All Announcements",values)
            setVerticalRecyclerView()

            if (values.size() != 0) {

                lastValue = values
                getMorePhonesFromFireStore()
            }

        }.addOnFailureListener { exception ->
            binding.allPhonesProgressBar.visibility = View.INVISIBLE
            context?.let { Toast.makeText(it,exception.localizedMessage,Toast.LENGTH_SHORT).show() }
        }
    }

    private fun getMorePhonesFromFireStore() {

        val scrollView = binding.nestedScrollView
        scrollView.viewTreeObserver.addOnScrollChangedListener {

            if (scrollView.getChildAt(0).bottom <= (scrollView.height + scrollView.scrollY)) {

                if (!announcementsAreOver) {

                    binding.morePhonesProgressBar.visibility = View.VISIBLE

                    firestore.collection("All Announcements")
                        .orderBy("time",Query.Direction.ASCENDING)
                        .startAfter(lastValue.documents[lastValue.size()-1])
                        .limit(numberOfAnnouncement).get().addOnSuccessListener { values ->

                            if (values.size() < numberOfAnnouncement) {
                                announcementsAreOver = true
                            }
                            binding.morePhonesProgressBar.visibility = View.INVISIBLE

                            lastValue = values

                            val morePhones = ArrayList<ModelAnnouncement>()
                                .getDataFromFireStore(firestore,"All Announcements",values)

                            for (i in 0 until morePhones.size) {

                                allPhones.add(morePhones[i])

                            }
                            verticalRecyclerViewAdapter.notifyDataSetChanged()
                        }
                }
            }
        }
    }

    private fun setHorizontalRecyclerView() {

        val hrv = binding.horizontalRecyclerView
        hrv.setHasFixedSize(true)
        hrv.layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL)
        val horizontalRecyclerViewAdapter = MostViewedPhonesAdapter(mostViewedPhones,this@HomeFragment)
        hrv.adapter = horizontalRecyclerViewAdapter
    }

    private fun setVerticalRecyclerView() {

        context?.let {

            val vrv = binding.verticalRecyclerView
            vrv.isNestedScrollingEnabled = false
            vrv.setHasFixedSize(true)
            vrv.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            verticalRecyclerViewAdapter = AllPhonesAdapter(it,allPhones,this@HomeFragment)
            vrv.adapter = verticalRecyclerViewAdapter
        }
    }

    override fun onMostViewedPhoneClick() {

        showDetailsFragmentAlgorithm()
    }

    override fun setOnClickVerticalAnnouncement() {

        showDetailsFragmentAlgorithm()
    }

    private fun showDetailsFragmentAlgorithm() {

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
        activity?.findViewById<NestedScrollView>(R.id.nestedScrollView)?.visibility = View.GONE
        binding.detailsFragmentContainer.visibility = View.VISIBLE

        val bundle = Bundle()
        bundle.putStringArrayList("images",allPhones[0].image)
        bundle.putBundle("announcementData",bundle)
        val detailsFragment = ShowDetailsFragment()
        detailsFragment.arguments = bundle


        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.detailsFragmentContainer,detailsFragment)
            ?.commit()

        onBackPressInDetailsScreen()
    }

    private fun onBackPressInDetailsScreen() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                binding.detailsFragmentContainer.visibility = View.GONE
                activity?.findViewById<NestedScrollView>(R.id.nestedScrollView)?.visibility = View.VISIBLE
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.VISIBLE
                onBackPressInHomeFragment()
            }
        })
    }

    private fun onBackPressInHomeFragment() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity?.startActivity(intent)
            }
        })
    }

}