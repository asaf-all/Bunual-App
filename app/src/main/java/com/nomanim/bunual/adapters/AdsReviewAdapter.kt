package com.nomanim.bunual.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bunual.databinding.LayoutCardViewAdsReviewBinding
import com.nomanim.bunual.models.ModelAdsReview

class AdsReviewAdapter(private val list: List<ModelAdsReview>, private val listener: Listener)
    : RecyclerView.Adapter<AdsReviewAdapter.Holder>() {

    interface Listener {}

    class Holder(val binding: LayoutCardViewAdsReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: List<ModelAdsReview>, position: Int, listener: Listener) {

            binding.reviewName.text = list[position].reviewName
            binding.reviewValue.text = list[position].reviewValue
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewAdsReviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(list,position,listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}