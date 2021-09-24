package com.nomanim.bax.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewPhoneModelsBinding
import com.nomanim.bax.retrofit.models.ModelPhoneModels
import com.squareup.picasso.Picasso

class PhoneModelsAdapter (val context: Context, private val list: ArrayList<ModelPhoneModels>, val listener: Listener)
    : RecyclerView.Adapter<PhoneModelsAdapter.Holder>() {


    interface Listener {

        fun onCardViewClickListener(modelName: String)

    }

    class Holder(val binding: LayoutCardViewPhoneModelsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<ModelPhoneModels>, position: Int, listener: Listener, context: Context) {

            binding.brandOrModelName.text = list[position].modelName
            Picasso.get().load(list[position].modelImage).into(binding.modelImageView)
            binding.modelOrBrandCardView.setOnClickListener { listener.onCardViewClickListener(list[position].modelName) }

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