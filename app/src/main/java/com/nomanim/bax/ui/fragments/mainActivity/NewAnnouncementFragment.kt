package com.nomanim.bax.ui.fragments.mainActivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bax.R
import com.nomanim.bax.databinding.FragmentNewAnnouncementBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.models.ModelPhone
import com.nomanim.bax.models.ModelUser
import com.nomanim.bax.retrofit.models.ModelPlace
import com.nomanim.bax.ui.activities.NewAnnouncementActivity

class NewAnnouncementFragment : Fragment() {

    private var _binding: FragmentNewAnnouncementBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentNewAnnouncementBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {

            val intent = Intent(requireActivity(),NewAnnouncementActivity::class.java)
            requireActivity().finish()
            requireActivity().startActivity(intent)

        }else { findNavController().navigate(R.id.action_newAnnouncementFragment_to_registrationFragment) }

        return binding.root
    }
}