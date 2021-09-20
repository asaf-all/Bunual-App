package com.nomanim.bax.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewPhoneStorageBinding

class FeaturesSheetRecyclerView(private val list: ArrayList<String>, val listener: Listener)
    : RecyclerView.Adapter<FeaturesSheetRecyclerView.Holder>() {

    interface Listener {

    }

    class Holder(val binding: LayoutCardViewPhoneStorageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<String>, position: Int, listener: Listener) {

            binding.ramTextView.text = list[position]

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewPhoneStorageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(list,position,listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}