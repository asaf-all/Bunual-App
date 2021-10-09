package com.nomanim.bax.ui.fragments.mainActivity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bax.R
import com.nomanim.bax.adapters.AllPhonesAdapter
import com.nomanim.bax.databinding.FragmentProfileBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.ui.other.getDataFromFireStore

class ProfileFragment : Fragment(),AllPhonesAdapter.Listener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentUser: FirebaseUser? = null
    private var announcements = ArrayList<ModelAnnouncement>()
    private lateinit var recyclerViewAdapter: AllPhonesAdapter
    private lateinit var lastValue: QuerySnapshot
    private var announcementsAreOver = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        _binding = FragmentProfileBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser

        if (currentUser != null) {

            binding.phoneNumberTextView.text = currentUser?.phoneNumber.toString()
            getCurrentUserAnnouncements()
            context?.let { setRecyclerView(it) }


        }else {

            findNavController().navigate(R.id.action_profileFragment_to_registrationFragment)
        }

        return binding.root

    }

    private fun getCurrentUserAnnouncements() {

        firestore.collection(currentUser?.phoneNumber.toString()).orderBy("time", Query.Direction.ASCENDING)
            .limit(10).addSnapshotListener { value, error ->

            announcements.getDataFromFireStore(firestore,currentUser?.phoneNumber.toString(),value)

            if (value?.size() != 0) {

                value?.let { lastValue = it }
                getMoreAnnouncements()
            }

        }

    }

    private fun getMoreAnnouncements() {

        binding.profilePhonesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {

                    Toast.makeText(requireContext(),"***",Toast.LENGTH_SHORT).show()

                    if (!announcementsAreOver) {

                        binding.currentUserProgressBar.visibility = View.VISIBLE

                        firestore.collection(currentUser?.phoneNumber.toString())
                            .orderBy("time", Query.Direction.ASCENDING)
                            .startAfter(lastValue.documents[lastValue.size()-1])
                            .limit(10).get().addOnSuccessListener { values ->

                                if (values.size() < 10) {

                                    announcementsAreOver = true
                                }
                                binding.currentUserProgressBar.visibility = View.INVISIBLE

                                lastValue = values

                                val morePhones = ArrayList<ModelAnnouncement>()
                                    .getDataFromFireStore(firestore,currentUser?.phoneNumber.toString(),values)

                                for (i in 0 until morePhones.size) {

                                    announcements.add(morePhones[i])

                                }
                                recyclerViewAdapter.notifyDataSetChanged()
                            }
                    }
                }
            }
        })
    }

    private fun setRecyclerView(context: Context) {

        val recyclerView = binding.profilePhonesRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewAdapter = AllPhonesAdapter(context,announcements,this@ProfileFragment)
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun setOnClickVerticalAnnouncement() {

    }
}