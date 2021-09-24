package com.nomanim.bax.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewPhoneModelsBinding
import com.nomanim.bax.retrofit.models.ModelPhoneBrands

class PhoneBrandsAdapter (val context: Context, private val list: ArrayList<ModelPhoneBrands>, val listener: Listener)
    : RecyclerView.Adapter<PhoneBrandsAdapter.Holder>() {

    interface Listener {

        fun onCardViewClickListener(brandId: String, brandName: String)

    }

    class Holder(val binding: LayoutCardViewPhoneModelsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<ModelPhoneBrands>, position: Int, listener: Listener, context: Context) {

            binding.brandOrModelName.text = list[position].brandName
            binding.modelImageView.visibility = View.INVISIBLE
            binding.modelOrBrandCardView.setOnClickListener { listener.onCardViewClickListener(list[position].id,list[position].brandName) }


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