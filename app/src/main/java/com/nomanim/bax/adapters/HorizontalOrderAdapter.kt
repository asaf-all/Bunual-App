package com.nomanim.bax.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewHorizontalPhonesBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.ui.other.downloadImageWithPicasso

class HorizontalOrderAdapter(private val list: ArrayList<ModelAnnouncement>, val listener: Listener)
    : RecyclerView.Adapter<HorizontalOrderAdapter.Holder>() {

    interface Listener {

        fun setOnClickHorizontalAnnouncement()
    }

    class Holder(val binding: LayoutCardViewHorizontalPhonesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<ModelAnnouncement>, position: Int, listener: Listener) {

            binding.mostViewedPhonesPrice.text = list[position].phone.price
            binding.mostViewedPhonesName.text = list[position].phone.brand
            binding.mostViewedPhonesViews.text = list[position].numberOfViews
            binding.phoneImage.downloadImageWithPicasso(list[position].image)
            binding.horizontalLinearLayout.setOnClickListener { listener.setOnClickHorizontalAnnouncement() }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewHorizontalPhonesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(list,position,listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}