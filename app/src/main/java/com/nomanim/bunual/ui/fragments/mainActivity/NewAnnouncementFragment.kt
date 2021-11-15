package com.nomanim.bunual.ui.fragments.mainActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentNewAnnouncementBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.models.ModelPhone
import com.nomanim.bunual.models.ModelUser
import com.nomanim.bunual.retrofit.models.ModelPlaces
import com.nomanim.bunual.room.database.RoomDB
import com.nomanim.bunual.ui.activities.NewAnnouncementActivity
import com.nomanim.bunual.ui.other.BaseCoroutineScope
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.coroutines.launch

class NewAnnouncementFragment : BaseCoroutineScope() {

    private var _binding: FragmentNewAnnouncementBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentNewAnnouncementBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()

        binding.newAdsImageView.visibility = View.INVISIBLE
        binding.newAdsTextView.visibility = View.INVISIBLE
        binding.buttonInNewAdsFragment.visibility = View.INVISIBLE


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateSomeFieldOfSharedPreferences()

        launch {

            val database = RoomDB(requireContext()).getDataFromRoom()
            database.deleteImagesUri()
            intentActivityOfNewAds()
        }

        /*if (auth.currentUser != null) {

            updateSomeFieldOfSharedPreferences()

            launch {

                val database = RoomDB(requireContext()).getDataFromRoom()
                database.deleteImagesUri()
                intentActivityOfNewAds()
            }

        }else {

            binding.newAdsImageView.visibility = View.VISIBLE
            binding.newAdsTextView.visibility = View.VISIBLE
            binding.buttonInNewAdsFragment.visibility = View.VISIBLE

            PushDownAnim.setPushDownAnimTo(binding.buttonInNewAdsFragment).setOnClickListener {

                findNavController().navigate(R.id.action_newAnnouncementFragment_to_profileFragment)
            }
        }*/
    }

    private fun updateSomeFieldOfSharedPreferences() {

        val sharedPref = activity?.getSharedPreferences("sharedPrefInNewAdsActivity", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putBoolean("imagesIsEmptyInRoom",true)
        editor?.putString("description","")
        editor?.putString("storageCapacity",getString(R.string.choose_phone_storage))
        editor?.putString("ramCapacity",getString(R.string.choose_phone_ram))
        editor?.putString("color",getString(R.string.choose_phone_color))
        editor?.putString("price","")
        editor?.apply()
    }

    private fun intentActivityOfNewAds() {

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.INVISIBLE
        val intent = Intent(activity, NewAnnouncementActivity::class.java)
        activity?.finish()
        activity?.startActivity(intent)
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun addAdsToFieStore() {

        val imagesList = ArrayList<String>()
        imagesList.add("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11-pro.jpg")
        imagesList.add("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11-pro-max-.jpg")
        imagesList.add("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg")

        val modelPhone = ModelPhone("Samsung","galaxy a71","423 AZN","purple","64 GB","4 GB","used","no",false)
        val modelPlaces = ModelPlaces("Baku","2M")
        val modelUser = ModelUser("randomName","randomNumber",modelPlaces)
        val modelAnnouncement = ModelAnnouncement("",imagesList,"randomDescription","524", Timestamp.now(),modelPhone,modelUser)

        firestore = FirebaseFirestore.getInstance()
        firestore.collection("All Announcements").add(modelAnnouncement)
            .addOnSuccessListener {

                Toast.makeText(requireContext(),"Added",Toast.LENGTH_SHORT).show()

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}