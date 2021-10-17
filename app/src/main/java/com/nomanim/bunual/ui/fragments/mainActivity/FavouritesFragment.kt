package com.nomanim.bunual.ui.fragments.mainActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.R
import com.nomanim.bunual.ui.adapters.AllPhonesAdapter
import com.nomanim.bunual.databinding.FragmentFavouritesBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.ui.other.getDataFromFireStore

class FavouritesFragment : Fragment(),AllPhonesAdapter.Listener {

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

        binding.noDataImageView.visibility = View.INVISIBLE
        binding.noDataTextView.visibility = View.INVISIBLE

        if (currentUser != null) { getFavouritesPhonesFromFireStore() }
        else {

            binding.noDataImageView.visibility = View.VISIBLE
            binding.noDataTextView.visibility = View.VISIBLE
            binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
        }

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

                    allFavouritePhones.getDataFromFireStore(firestore,resources.getString(R.string.all_announcements),values)
                    binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
                    setFavoritesPhonesRecyclerView()

                }.addOnFailureListener {

                    binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(),resources.getString(R.string.fail),Toast.LENGTH_SHORT).show()
                }

            }else {

                binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
                binding.noDataImageView.visibility = View.VISIBLE
                binding.noDataTextView.visibility = View.VISIBLE
            }
        }.addOnFailureListener {

            binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun setFavoritesPhonesRecyclerView() {

        val fp = binding.favoritesPhones
        fp.isNestedScrollingEnabled = false
        fp.setHasFixedSize(true)
        fp.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AllPhonesAdapter(requireContext(),allFavouritePhones,this@FavouritesFragment)
        fp.adapter = adapter
    }

    override fun setOnClickVerticalAnnouncement(position: Int) {

    }

}