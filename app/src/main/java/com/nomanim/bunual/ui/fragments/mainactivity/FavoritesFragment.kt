package com.nomanim.bunual.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.AllPhonesAdapter
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.base.responseToList
import com.nomanim.bunual.databinding.FragmentFavoritesBinding
import com.nomanim.bunual.viewmodel.FavoritesViewModel
import gun0912.tedimagepicker.util.ToastUtil

class FavoritesFragment : Fragment(), AllPhonesAdapter.Listener {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val mFavoritesViewModel: FavoritesViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var allFavoritePhones = ArrayList<ModelAnnouncement>()
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        mFavoritesViewModel.getIds(firestore, auth.currentUser?.phoneNumber.toString())
        initFavoritesViewModel()
    }

    private fun initFavoritesViewModel() {
        mFavoritesViewModel.idsLiveData().observe(viewLifecycleOwner, { response ->
            if (response.size() != 0) {
                val list = ArrayList<String>()
                for (doc in response.documents) {
                    val originalAnnouncementId = doc.get("originalAnnouncementId") as String
                    list.add(originalAnnouncementId)
                    mFavoritesViewModel.getFavorites(firestore, list)
                }
            }
        })
        mFavoritesViewModel.favoritesLiveData().observe(viewLifecycleOwner, { response ->
            allFavoritePhones.responseToList(
                firestore,
                resources.getString(R.string.all_announcements),
                response
            )
            binding.progressBar.visibility = View.INVISIBLE
            setFavoritesPhonesRecyclerView()
        })
        mFavoritesViewModel.errorMutableLiveData.observe(viewLifecycleOwner, { message ->
            binding.progressBar.visibility = View.INVISIBLE
            ToastUtil.showToast("error: $message")
        })
    }

    private fun setFavoritesPhonesRecyclerView() {
        val fp = binding.favoritesPhones
        fp.isNestedScrollingEnabled = false
        fp.setHasFixedSize(true)
        fp.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AllPhonesAdapter(requireContext(), allFavoritePhones, this@FavoritesFragment)
        fp.adapter = adapter
    }

    override fun setOnClickVerticalAnnouncement(list: ArrayList<ModelAnnouncement>, position: Int) {

    }

}