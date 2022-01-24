package com.nomanim.bunual.ui.fragments.mainactivity

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.Constants
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.AllPhonesAdapter
import com.nomanim.bunual.base.BaseFragment
import com.nomanim.bunual.base.responseToList
import com.nomanim.bunual.databinding.FragmentResultBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.ui.fragments.newadsactivity.FeaturesFragmentDirections
import com.nomanim.bunual.viewmodel.ResultViewModel
import kotlinx.parcelize.Parcelize


@Parcelize
enum class ResultType : Parcelable {
    MOST_VIEWED
}


class ResultFragment : BaseFragment(), AllPhonesAdapter.Listener {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ResultFragmentArgs>()

    private val mResultViewModel: ResultViewModel by viewModels()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerViewAdapter: AllPhonesAdapter
    private val mostViewedAds = ArrayList<ModelAnnouncement>()
    private lateinit var lastValue: QuerySnapshot

    private var adsAreOver = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        firestore = FirebaseFirestore.getInstance()


        pressBackButton()
        initViewModel()

        when (args.resultType) {
            ResultType.MOST_VIEWED -> {
                binding.txtTitle.text = getString(R.string.most_viewed)
                mResultViewModel.getMostViewedAds(firestore, Constants.NUMBER_OF_ADS)
            }
            else -> {}
        }
    }

    private fun initViewModel() {
        mResultViewModel.mostViewedLiveData().observe(viewLifecycleOwner, { response ->
            binding.progressBar.visibility = View.INVISIBLE
            mostViewedAds.responseToList(firestore, Constants.ADS_COLLECTION_NAME, response)
            setResultRV()
            if (response.size() != 0) {
                lastValue = response
                getMoreMostViewedAds()
            }
        })

        mResultViewModel.moreMostViewedLiveData().observe(viewLifecycleOwner, { response ->
            if (response.size() < Constants.NUMBER_OF_ADS) {
                adsAreOver = true
            }
            binding.progressBar.visibility = View.INVISIBLE
            lastValue = response
            val moreAds = ArrayList<ModelAnnouncement>()
                .responseToList(firestore, Constants.ADS_COLLECTION_NAME, response)
            for (element in moreAds) {
                mostViewedAds.add(element)
            }
            recyclerViewAdapter.notifyDataSetChanged()
        })
        mResultViewModel.errorLiveData().observe(viewLifecycleOwner, { message ->
            binding.progressBar.visibility = View.INVISIBLE
            showToastMessage("error: $message")
        })
    }

    private fun setResultRV() {
        recyclerViewAdapter = AllPhonesAdapter(
            requireContext(),
            mostViewedAds,
            this@ResultFragment
        ) { model ->
            mMainActivity.intentToAdsDetails(model)
        }
        val fp = binding.resultRV
        fp.isNestedScrollingEnabled = false
        fp.setHasFixedSize(true)
        fp.layoutManager = LinearLayoutManager(requireContext())
        fp.adapter = recyclerViewAdapter
    }

    private fun getMoreMostViewedAds() {
        val scrollView = binding.nestedScrollView
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.getChildAt(0).bottom <= (scrollView.height + scrollView.scrollY)) {
                if (!adsAreOver) {
                    binding.progressBar.visibility = View.VISIBLE
                    mResultViewModel.getMoreMostViewedAds(firestore, lastValue, Constants.NUMBER_OF_ADS)
                }
            }
        }
    }

    private fun pressBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(
                        R.id.action_resultFragment_to_homeFragment
                    )
                }
            })
    }

}