package com.nomanim.bax.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewMostviewedPhonesBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.ui.other.downloadImageWithGlide
import com.thekhaeng.pushdownanim.PushDownAnim

class MostViewedPhonesAdapter(private val list: ArrayList<ModelAnnouncement>, val listener: Listener)
    : RecyclerView.Adapter<MostViewedPhonesAdapter.Holder>() {

    interface Listener {

        fun onMostViewedPhoneClick()
    }

    class Holder(val binding: LayoutCardViewMostviewedPhonesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<ModelAnnouncement>, position: Int, listener: Listener) {

            binding.mostViewedPhonesPrice.text = list[position].phone.price
            binding.mostViewedPhonesName.text = list[position].phone.brand
            binding.mostViewedPhonesViews.text = list[position].numberOfViews
            binding.phoneImage.downloadImageWithGlide(binding.root,list[position].image[0])
            PushDownAnim.setPushDownAnimTo(binding.mostViewedPhonesLinearLayout).setOnClickListener {

                listener.onMostViewedPhoneClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewMostviewedPhonesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(list,position,listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}