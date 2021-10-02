package com.nomanim.bax.ui.fragments.mainActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nomanim.bax.adapters.PhoneReviewsAdapter
import com.nomanim.bax.databinding.FragmentShowDetailsBinding
import com.nomanim.bax.models.ModelPhoneReviews


class ShowDetailsFragment : Fragment(), PhoneReviewsAdapter.Listener {

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!
    private val reviewsList = ArrayList<ModelPhoneReviews>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentShowDetailsBinding.inflate(inflater)

        val modelReviews1 = ModelPhoneReviews("storage","32 GB")
        val modelReviews2 = ModelPhoneReviews("ram","2 GB")

        reviewsList.add(modelReviews1)
        reviewsList.add(modelReviews2)

        setReviewsRecyclerView()



        return binding.root
    }

    private fun setReviewsRecyclerView() {

        context?.let {

            val recyclerView = binding.phoneReviewsRecyclerView
            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false
            val adapter = PhoneReviewsAdapter(it,reviewsList,this@ShowDetailsFragment)
            recyclerView.adapter = adapter
        }
    }

}