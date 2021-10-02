package com.nomanim.bax.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewPhoneReviewsBinding
import com.nomanim.bax.models.ModelPhoneReviews

class PhoneReviewsAdapter (val context: Context, private val list: ArrayList<ModelPhoneReviews>, val listener: Listener)
    : RecyclerView.Adapter<PhoneReviewsAdapter.Holder>() {


    interface Listener {

    }

    class Holder(val binding: LayoutCardViewPhoneReviewsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<ModelPhoneReviews>, position: Int, listener: Listener, context: Context) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewPhoneReviewsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(list,position,listener,context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}