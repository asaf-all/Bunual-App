package com.nomanim.bunual.ui.fragments.mainActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.MostViewedPhonesAdapter
import com.nomanim.bunual.adapters.AllPhonesAdapter
import com.nomanim.bunual.databinding.FragmentHomeBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.ui.activities.AdsDetailsActivity
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import com.nomanim.bunual.ui.other.getDataFromFireStore
import com.thekhaeng.pushdownanim.PushDownAnim
import java.util.*
import kotlin.collections.ArrayList
class HomeFragment : BaseCoroutineScope(),MostViewedPhonesAdapter.Listener,AllPhonesAdapter.Listener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var sharedPref: SharedPreferences? = null
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        currentUserPhoneNumber = auth.currentUser?.phoneNumber.toString()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.status_bar_color)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.VISIBLE

        checkCodeForNavigateProfileFragment()
        filterPhonesWithModelOrBrandNames()
        getMostViewedPhonesFromFireStore()
        getAllPhonesFromFireStore()
    }

    private fun checkCodeForNavigateProfileFragment() {

        val toProfileFragment = sharedPref?.getBoolean("toProfileFragment",false)

        if (toProfileFragment != null && toProfileFragment) {

            sharedPref?.edit()?.putBoolean("toProfileFragment",false)?.apply()
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun filterPhonesWithModelOrBrandNames() {

        PushDownAnim.setPushDownAnimTo(binding.cardView2).setOnClickListener {

            Snackbar.make(binding.root,"will be activate",Snackbar.LENGTH_SHORT).show()

            /*activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.detailsFragmentContainer,BrandsFragment())
                ?.commit()

            binding.nestedScrollView.visibility = View.GONE
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
            binding.detailsFragmentContainer.visibility = View.VISIBLE*/
        }
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

    override fun onMostViewedPhoneClick(list: ArrayList<ModelAnnouncement>,position: Int) {

        intentToAdsDetailsActivityWithData(list, position)
    }

    override fun setOnClickVerticalAnnouncement(list: ArrayList<ModelAnnouncement>, position: Int) {

        intentToAdsDetailsActivityWithData(list,position)
    }

    private fun intentToAdsDetailsActivityWithData(list: ArrayList<ModelAnnouncement>,position: Int) {

        val intent = Intent(requireContext(),AdsDetailsActivity::class.java)
        intent.putExtra("imagesLinks",createListWithSelectedAdsImages(list, position).toString())
        intent.putExtra("selectedAnnouncementId",list[position].id)
        //intent.putExtra("allData",list[position])
        activity?.startActivity(intent)
    }

    private fun createListWithSelectedAdsImages(list: ArrayList<ModelAnnouncement>, position: Int) : StringBuilder {

        val stringBuilder = StringBuilder()

        for (i in 0 until list[position].image.size) {

            if (i == list[position].image.size-1) {

                stringBuilder.append(list[position].image[i])

            }else { stringBuilder.append(list[position].image[i] + "|") }
        }
        return stringBuilder
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}