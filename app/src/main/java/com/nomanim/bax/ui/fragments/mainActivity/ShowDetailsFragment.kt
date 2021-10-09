package com.nomanim.bax.ui.fragments.mainActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bax.adapters.SimilarPhonesAdapter
import com.nomanim.bax.databinding.FragmentShowDetailsBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.ui.other.getDataFromFireStore


class ShowDetailsFragment : Fragment(), SimilarPhonesAdapter.Listener {

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private val similarPhones = ArrayList<ModelAnnouncement>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentShowDetailsBinding.inflate(inflater)
        firestore = FirebaseFirestore.getInstance()

        getSimilarPhonesFromFireStore()



        return binding.root
    }

    private fun getSimilarPhonesFromFireStore() {

        firestore.collection("All Announcements").get().addOnSuccessListener { value ->

            similarPhones.getDataFromFireStore(firestore,"All Announcements",value)
            setSimilarPhonesRecyclerView()
        }
    }

    private fun setSimilarPhonesRecyclerView() {

        context?.let {

            val recyclerView = binding.similarPhonesRecyclerView
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL)
            val adapter = SimilarPhonesAdapter(it,similarPhones,this@ShowDetailsFragment)
            recyclerView.adapter = adapter
        }
    }

    override fun onSimilarPhoneClick() {

    }

}