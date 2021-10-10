package com.nomanim.bax.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewAllPhonesBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.ui.other.downloadImageWithGlide
import com.thekhaeng.pushdownanim.PushDownAnim

class AllPhonesAdapter (val context: Context, val list: ArrayList<ModelAnnouncement>, val listener: Listener)
    : RecyclerView.Adapter<AllPhonesAdapter.Holder>() {

    interface Listener {

        fun setOnClickVerticalAnnouncement()

    }


    class Holder(val binding: LayoutCardViewAllPhonesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<ModelAnnouncement>, position: Int, listener: Listener) {

            binding.announcementPrice.text = list[position].phone.price
            binding.announcementName.text = list[position].phone.brand
            binding.announcementDescription.text = list[position].description
            binding.announcementViews.text = list[position].numberOfViews
            //val announcementUploadingTime = com.google.firebase.Timestamp.now().toDate()
            //holder.allPhonesTime.text = announcementUploadingTime.toString()
            binding.announcementImageView.downloadImageWithGlide(binding.root,list[position].image[0])
            PushDownAnim.setPushDownAnimTo(binding.verticalLinearLayout).setOnClickListener {

                listener.setOnClickVerticalAnnouncement()
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutCardViewAllPhonesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.run(list,position,listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}