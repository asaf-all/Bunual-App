package com.nomanim.bax.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewPhoneModelsBinding
import com.nomanim.bax.retrofit.models.PhoneBrandName

class PhoneBrandRecyclerView (val context: Context, private val list: ArrayList<PhoneBrandName>, val listener: Listener)
    : RecyclerView.Adapter<PhoneBrandRecyclerView.Holder>() {

    interface Listener {

        fun onCardViewClickListener(brandId: String)

    }

    class Holder(val binding: LayoutCardViewPhoneModelsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<PhoneBrandName>, position: Int, listener: Listener, context: Context) {

            binding.brandOrModelName.text = list[position].brandName
            binding.modelOrBrandCardView.setOnClickListener { listener.onCardViewClickListener(list[position].id) }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewPhoneModelsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(list,position,listener,context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}