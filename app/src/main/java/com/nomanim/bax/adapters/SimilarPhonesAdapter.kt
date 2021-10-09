package com.nomanim.bax.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewSimilarPhonesBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.ui.other.downloadImageWithGlide

class SimilarPhonesAdapter (val context: Context, private val list: ArrayList<ModelAnnouncement>, val listener: Listener)
    : RecyclerView.Adapter<SimilarPhonesAdapter.Holder>() {

    interface Listener {

        fun onSimilarPhoneClick()
    }

    class Holder(val binding: LayoutCardViewSimilarPhonesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<ModelAnnouncement>, position: Int, listener: Listener, context: Context) {

            binding.mostViewedPhonesPrice.text = list[position].phone.price
            binding.mostViewedPhonesName.text = list[position].phone.brand
            binding.mostViewedPhonesViews.text = list[position].numberOfViews
            binding.phoneImage.downloadImageWithGlide(binding.root,list[position].image[0])
            binding.similarPhonesLinearLayout.setOnClickListener { listener.onSimilarPhoneClick() }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewSimilarPhonesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(list,position,listener,context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}