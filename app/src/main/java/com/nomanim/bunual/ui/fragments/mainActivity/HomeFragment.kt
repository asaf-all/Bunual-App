package com.nomanim.bunual.ui.fragments.mainActivity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.adapters.MostViewedPhonesAdapter
import com.nomanim.bunual.ui.adapters.AllPhonesAdapter
import com.nomanim.bunual.databinding.FragmentHomeBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.ui.activities.AdsDetailsActivity
import com.nomanim.bunual.ui.fragments.newAnnouncementActivity.BrandsFragment
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

        filterPhonesWithModelOrBrandNames()
        getMostViewedPhonesFromFireStore()
        getAllPhonesFromFireStore()

        return binding.root
    }

    private fun filterPhonesWithModelOrBrandNames() {

        PushDownAnim.setPushDownAnimTo(binding.filterPhonesLayout).setOnClickListener {

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.detailsFragmentContainer,BrandsFragment())
                ?.commit()

            binding.nestedScrollView.visibility = View.GONE
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
            binding.detailsFragmentContainer.visibility = View.VISIBLE
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

        intentToAdsDetailsActivityWithData(list,position)
    }

    override fun setOnClickVerticalAnnouncement(list: ArrayList<ModelAnnouncement>, position: Int) {

        intentToAdsDetailsActivityWithData(list,position)
    }

    private fun intentToAdsDetailsActivityWithData(list: ArrayList<ModelAnnouncement>,position: Int) {

        val intent = Intent(requireContext(),AdsDetailsActivity::class.java)
        intent.putExtra("selectedAnnouncementId",list[position].id)
        intent.putExtra("imagesLinks",createListWithSelectedAdsImages(position).toString())
        activity?.startActivity(intent)
    }

    private fun createListWithSelectedAdsImages(position: Int) : StringBuilder {

        val stringBuilder = StringBuilder()

        for (i in 0 until allPhones[position].image.size) {

            if (i == allPhones[position].image.size-1) {

                stringBuilder.append(allPhones[position].image[i])

            }else { stringBuilder.append(allPhones[position].image[i] + "|") }
        }
        return stringBuilder
    }



    /*private fun showDetailsFragmentAlgorithm(list: ArrayList<ModelAnnouncement>, position: Int) {

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
        binding.nestedScrollView.visibility = View.GONE
        binding.detailsFragmentContainer.visibility = View.VISIBLE
        pressBackButton()
        addImagesLinkToSharedPref(position)
        addSelectedAnnouncementIdToSharedPref(list,position)
        showDetailsFragment()
    }



    private fun addSelectedAnnouncementIdToSharedPref(list: ArrayList<ModelAnnouncement>, position: Int) {

        val selectedAnnouncementId = list[position].id

        val sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putString("selected_announcement_id",selectedAnnouncementId)
        editor?.apply()
    }

    private fun showDetailsFragment() {

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.detailsFragmentContainer,ShowDetailsFragment())
            ?.commit()
    }

    private fun pressBackButton() {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (binding.nestedScrollView.visibility == View.VISIBLE) {

                    pressBackButtonInHomeFragment() } else { pressBackButtonInDetailsScreen() }
            }
        })
    }

    private fun pressBackButtonInDetailsScreen() {

        binding.detailsFragmentContainer.visibility = View.GONE
        activity?.findViewById<NestedScrollView>(R.id.nestedScrollView)?.visibility = View.VISIBLE
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.VISIBLE
    }

    private fun pressBackButtonInHomeFragment() {

        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity?.startActivity(intent)
    }*/

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}