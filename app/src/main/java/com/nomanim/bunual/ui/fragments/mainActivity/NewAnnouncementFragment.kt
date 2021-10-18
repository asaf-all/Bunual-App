package com.nomanim.bunual.ui.fragments.mainActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.FragmentNewAnnouncementBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.models.ModelPhone
import com.nomanim.bunual.models.ModelUser
import com.nomanim.bunual.retrofit.models.ModelPlaces
import com.nomanim.bunual.ui.activities.NewAnnouncementActivity
import com.thekhaeng.pushdownanim.PushDownAnim

class NewAnnouncementFragment : Fragment() {

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

        if (auth.currentUser != null) {

            val intent = Intent(activity, NewAnnouncementActivity::class.java)
            activity?.finish()
            activity?.startActivity(intent)

            /*val imagesList = ArrayList<String>()
            imagesList.add("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11-pro.jpg")
            imagesList.add("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11-pro-max-.jpg")
            imagesList.add("https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-11.jpg")

            val modelPhone = ModelPhone("Samsung","galaxy a71","423 AZN","purple","64 GB","4 GB","used","no",false)
            val modelPlaces = ModelPlaces("Baku","2M")
            val modelUser = ModelUser("randomName","randomNumber",modelPlaces)
            val modelAnnouncement = ModelAnnouncement("",imagesList,"randomDescription","524", Timestamp.now(),"124",modelPhone,modelUser)

            firestore = FirebaseFirestore.getInstance()
            firestore.collection("All Announcements").add(modelAnnouncement)
                .addOnSuccessListener {

                    Toast.makeText(requireContext(),"Added",Toast.LENGTH_SHORT).show()

                }*/

        }else {

            binding.newAdsImageView.visibility = View.VISIBLE
            binding.newAdsTextView.visibility = View.VISIBLE
            binding.buttonInNewAdsFragment.visibility = View.VISIBLE

            PushDownAnim.setPushDownAnimTo(binding.buttonInNewAdsFragment).setOnClickListener {

                findNavController().navigate(R.id.action_newAnnouncementFragment_to_profileFragment)
            }
        }
        return binding.root
    }
}