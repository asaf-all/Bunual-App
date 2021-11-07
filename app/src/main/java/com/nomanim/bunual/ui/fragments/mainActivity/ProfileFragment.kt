package com.nomanim.bunual.ui.fragments.mainActivity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowdfire.cfalertdialog.CFAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.adapters.AllPhonesAdapter
import com.nomanim.bunual.databinding.FragmentProfileBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.retrofit.builders.SimpleDataApi
import com.nomanim.bunual.retrofit.models.ModelSimpleData
import com.nomanim.bunual.ui.activities.MainActivity
import com.nomanim.bunual.ui.other.getDataFromFireStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(),AllPhonesAdapter.Listener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentUser: FirebaseUser? = null
    private var sharedPref: SharedPreferences? = null
    private var announcements = ArrayList<ModelAnnouncement>()
    private lateinit var recyclerViewAdapter: AllPhonesAdapter
    private lateinit var lastValue: QuerySnapshot
    private var announcementsAreOver = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        _binding = FragmentProfileBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser
        sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)

        binding.withOfflineModeLayout.visibility = View.INVISIBLE
        binding.withoutOfflineModeLayout.visibility = View.INVISIBLE
        binding.noDataImageView.visibility = View.INVISIBLE
        binding.noDataTextView.visibility = View.INVISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentUser != null) {

            binding.logoutTextView.setOnClickListener { pressLogOutAlgorithm() }
            setUserPhoneNumber()
            checkInternetConnection()

        }else { findNavController().navigate(R.id.action_profileFragment_to_registrationFragment) }
    }

    private fun checkInternetConnection() {

        val simpleDataService = SimpleDataApi.builder.getData()
        simpleDataService.enqueue(object : Callback<ModelSimpleData> {
            override fun onResponse(call: Call<ModelSimpleData>, response: Response<ModelSimpleData>) {

                binding.withoutOfflineModeLayout.visibility = View.VISIBLE
                binding.withOfflineModeLayout.visibility = View.GONE

                getCurrentUserAnnouncements()
            }

            override fun onFailure(call: Call<ModelSimpleData>, t: Throwable) {

                binding.withoutOfflineModeLayout.visibility = View.GONE
                binding.withOfflineModeLayout.visibility = View.VISIBLE
            }
        })
    }

    private fun getCurrentUserAnnouncements() {

        firestore.collection(currentUser?.phoneNumber.toString()).orderBy("time", Query.Direction.ASCENDING)
            .limit(10).addSnapshotListener { value, error ->

                if (error != null) { Toast.makeText(requireContext(),R.string.fail, Toast.LENGTH_SHORT).show() }

                if (value?.size() != 0) {

                    announcements.getDataFromFireStore(firestore,currentUser?.phoneNumber.toString(),value)
                    setRecyclerView()

                    value?.let { lastValue = it }
                    getMoreAnnouncements()

                }else {

                    binding.noDataImageView.visibility = View.VISIBLE
                    binding.noDataTextView.visibility = View.VISIBLE
                    binding.currentUserProgressBar.visibility = View.INVISIBLE
                }
            }
    }

    private fun getMoreAnnouncements() {

        binding.profilePhonesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {

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

    private fun setRecyclerView() {

        val recyclerView = binding.profilePhonesRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewAdapter = AllPhonesAdapter(requireContext(),announcements,this@ProfileFragment)
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun setUserPhoneNumber() {

        val userPhoneNumber = sharedPref?.getString("userPhoneNumber",null)

        if (userPhoneNumber == null) {

            val currentPhoneNumber = currentUser?.phoneNumber.toString()
            val editor = sharedPref?.edit()
            editor?.putString("userPhoneNumber",currentPhoneNumber)
            editor?.apply()
            binding.phoneNumberTextView.text = currentPhoneNumber

        }else { binding.phoneNumberTextView.text = userPhoneNumber }
    }

    private fun pressLogOutAlgorithm() {

        showAlertDialog()
    }

    private fun showAlertDialog() {

        val builder = CFAlertDialog.Builder(requireContext())
            .setTitle(R.string.are_you_sure)
            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
            .addButton(getString(R.string.log_out),
                ContextCompat.getColor(requireContext(), R.color.white)
                , ContextCompat.getColor(requireContext(), R.color.cancel_button_color_red)
                , CFAlertDialog.CFAlertActionStyle.POSITIVE
                , CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
                DialogInterface.OnClickListener { dialog, which ->

                    auth.signOut()
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_profileFragment_to_homeFragment)

                })
            .addButton(getString(R.string.dismiss),
                ContextCompat.getColor(requireContext(), R.color.white)
                , ContextCompat.getColor(requireContext(),R.color.dismiss_button_color_green)
                , CFAlertDialog.CFAlertActionStyle.NEGATIVE
                , CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
                DialogInterface.OnClickListener { dialog, which ->

                    dialog.dismiss()
                })

        builder.show()
    }

    override fun setOnClickVerticalAnnouncement(list: ArrayList<ModelAnnouncement>, position: Int) {

    }
}