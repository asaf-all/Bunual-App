package com.nomanim.bax.ui.fragments.mainActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bax.R
import com.nomanim.bax.adapters.VerticalOrderAdapter
import com.nomanim.bax.databinding.FragmentFavouritesBinding
import com.nomanim.bax.firebase.Service
import com.nomanim.bax.models.ModelAnnouncement

class FavouritesFragment : Fragment(),VerticalOrderAdapter.Listener {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var allFavouritePhones = ArrayList<ModelAnnouncement>()
    private var favoritesPhones = ArrayList<String>()
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFavouritesBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser

        if (currentUser != null) { getFavouritesPhonesFromFireStore() }
        else { findNavController().navigate(R.id.action_favouritesFragment_to_registrationFragment) }

        return binding.root
    }

    private fun getFavouritesPhonesFromFireStore() {

        firestore.collection(currentUser?.phoneNumber.toString()).get().addOnSuccessListener { value ->

            if (value.size() != 0) {

                for (doc in value.documents) {

                    val originalAnnouncementId = doc.get("originalAnnouncementId") as String

                    favoritesPhones.add(originalAnnouncementId)
                }

                firestore.collection(resources.getString(R.string.all_announcements)).whereIn("id",favoritesPhones).get().addOnSuccessListener { values ->

                    allFavouritePhones = Service().getData(firestore,resources.getString(R.string.all_announcements),values)
                    binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
                    setFavoritesPhonesRecyclerView()

                }.addOnFailureListener {

                    binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(),resources.getString(R.string.fail),Toast.LENGTH_SHORT).show()
                }

            }else {

                binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
            }
        }.addOnFailureListener {

            binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun setFavoritesPhonesRecyclerView() {

        val fp = binding.favoritesPhones
        fp.isNestedScrollingEnabled = false
        fp.setHasFixedSize(true)
        fp.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adapter = VerticalOrderAdapter(requireContext(),allFavouritePhones,this@FavouritesFragment)
        fp.adapter = adapter
    }

}