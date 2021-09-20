package com.nomanim.bax.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewSortPhonesBinding

class SortRecyclerView (private val list:List<String>, val listener:Listener)
    : RecyclerView.Adapter<SortRecyclerView.Holder>() {

    interface Listener {

    }


    class Holder(val holder: LayoutCardViewSortPhonesBinding) : RecyclerView.ViewHolder(holder.root) {

        fun run(list: List<String>, position: Int) {

            holder.sortName.text = list[position]

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewSortPhonesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(list,position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}