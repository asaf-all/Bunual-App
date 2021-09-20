package com.nomanim.bax.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewPhoneModelsBinding
import com.nomanim.bax.retrofit.models.PhoneModelName

class PhoneModelRecyclerView (val context: Context, private val models: ArrayList<PhoneModelName>, val listener: Listener)
    : RecyclerView.Adapter<PhoneModelRecyclerView.Holder>() {


    interface Listener {

        fun onCardViewClickListener()

    }

    class Holder(val binding: LayoutCardViewPhoneModelsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(models: ArrayList<PhoneModelName>, position: Int, listener: Listener,context: Context) {

            binding.brandOrModelName.text = models[position].modelName
            binding.modelOrBrandCardView.setOnClickListener { listener.onCardViewClickListener() }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewPhoneModelsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(models,position,listener,context)
    }

    override fun getItemCount(): Int {
        return models.size
    }

}