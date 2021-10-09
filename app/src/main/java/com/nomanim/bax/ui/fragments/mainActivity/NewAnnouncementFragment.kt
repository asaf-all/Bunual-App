package com.nomanim.bax.ui.fragments.mainActivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentNewAnnouncementBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.models.ModelPhone
import com.nomanim.bax.models.ModelUser
import com.nomanim.bax.retrofit.models.ModelPlaces
import com.nomanim.bax.ui.activities.NewAnnouncementActivity

class NewAnnouncementFragment : Fragment() {

    private var _binding: FragmentNewAnnouncementBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentNewAnnouncementBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {

            val intent = Intent(activity, NewAnnouncementActivity::class.java)
            activity?.finish()
            activity?.startActivity(intent)

                /*val imagesList = ArrayList<String>()
            imagesList.add("https://fdn2.gsmarena.com/vv/bigpic/no3210b.gif")

                val modelPhone = ModelPhone("Samsung","galaxy a71","423 AZN","purple"
                ,"64 GB","4 GB","used","no",false)

            val modelPlaces = ModelPlaces("Baku","2M")

            val modelUser = ModelUser("randomName","randomNumber",modelPlaces)

            val modelAnnouncement = ModelAnnouncement("",imagesList,"randomDescription"
                ,"524", Timestamp.now(),"124",modelPhone,modelUser)

            firestore = FirebaseFirestore.getInstance()
            firestore.collection("All Announcements").add(modelAnnouncement)
                .addOnSuccessListener {

                Toast.makeText(requireContext(),"Added",Toast.LENGTH_SHORT).show()

            }*/

        }else { findNavController().navigate(R.id.action_newAnnouncementFragment_to_registrationFragment) }

        return binding.root
    }
}