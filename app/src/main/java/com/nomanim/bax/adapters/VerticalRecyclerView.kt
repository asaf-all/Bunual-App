package com.nomanim.bax.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewVerticalPhonesBinding
import com.nomanim.bax.models.ModelAnnouncement

class VerticalRecyclerView (val context: Context, val list: ArrayList<ModelAnnouncement>, val listener: Listener)
    : RecyclerView.Adapter<VerticalRecyclerView.Holder>() {

    interface Listener {

    }


    class Holder(val binding: LayoutCardViewVerticalPhonesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<ModelAnnouncement>, position: Int, listener: Listener) {

            binding.allPhonesPrice.text = list[position].phone.price
            binding.allPhonesName.text = list[position].phone.brand
            binding.allPhonesDescription.text = list[position].description
            binding.allPhonesViews.text = list[position].numberOfViews
            //val announcementUploadingTime = com.google.firebase.Timestamp.now().toDate()
            //holder.allPhonesTime.text = announcementUploadingTime.toString()

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutCardViewVerticalPhonesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.run(list,position,listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}