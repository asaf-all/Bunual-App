package com.nomanim.bunual.ui.fragments.mainactivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.AllPhonesAdapter
import com.nomanim.bunual.api.builders.RetrofitBuilder
import com.nomanim.bunual.databinding.FragmentFavouritesBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.api.entity.ModelSimpleData
import com.nomanim.bunual.base.responseToList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment(),AllPhonesAdapter.Listener {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private  lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var sharedPref: SharedPreferences? = null
    private var allFavouritePhones = ArrayList<ModelAnnouncement>()
    private var favoritesPhones = ArrayList<String>()
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFavouritesBinding.inflate(inflater,container,false)

        binding.withOfflineModeLayout.visibility = View.INVISIBLE
        binding.withoutOfflineModeLayout.visibility = View.INVISIBLE
        binding.noDataLayout.visibility = View.INVISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser
        sharedPref = activity?.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)

        checkInternetConnection()
    }

    private fun checkInternetConnection() {

        val simpleDataService = RetrofitBuilder.service.getSimpleData()
        simpleDataService.enqueue(object : Callback<ModelSimpleData> {
            override fun onResponse(call: Call<ModelSimpleData>, response: Response<ModelSimpleData>) {

                binding.withoutOfflineModeLayout.visibility = View.VISIBLE
                binding.withOfflineModeLayout.visibility = View.GONE

                if (currentUser != null) {

                    getFavouritesPhonesFromFireStore()
                }

                else {

                    binding.noDataLayout.visibility = View.VISIBLE
                    binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
                }
            }

            override fun onFailure(call: Call<ModelSimpleData>, t: Throwable) {

                binding.noDataLayout.visibility = View.INVISIBLE
                binding.withoutOfflineModeLayout.visibility = View.GONE
                binding.withOfflineModeLayout.visibility = View.VISIBLE
                binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
            }
        })
    }

    private fun getFavouritesPhonesFromFireStore() {

        firestore.collection(currentUser?.phoneNumber.toString()).get().addOnSuccessListener { value ->

            if (value.size() != 0) {

                for (doc in value.documents) {

                    val originalAnnouncementId = doc.get("originalAnnouncementId") as String

                    favoritesPhones.add(originalAnnouncementId)
                }

                if (context != null) {

                    firestore.collection(resources.getString(R.string.all_announcements)).whereIn("id",favoritesPhones).get().addOnSuccessListener { values ->

                        allFavouritePhones.responseToList(firestore,resources.getString(R.string.all_announcements),values)
                        binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
                        setFavoritesPhonesRecyclerView()

                    }.addOnFailureListener {

                        binding.failText.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.INVISIBLE
                        binding.withOfflineModeLayout.visibility = View.INVISIBLE
                        binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(),resources.getString(R.string.fail),Toast.LENGTH_SHORT).show()
                    }
                }

            }else {

                binding.favoritesPhonesProgressBar.visibility = View.INVISIBLE
                binding.noDataLayout.visibility = View.VISIBLE
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
        val adapter = AllPhonesAdapter(requireContext(),allFavouritePhones,this@FavoritesFragment)
        fp.adapter = adapter
    }

    override fun setOnClickVerticalAnnouncement(list: ArrayList<ModelAnnouncement>, position: Int) {

    }

}