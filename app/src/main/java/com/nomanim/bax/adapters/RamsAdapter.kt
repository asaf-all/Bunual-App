package com.nomanim.bax.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bax.databinding.LayoutCardViewPhoneStorageBinding

class RamsAdapter(private val list: ArrayList<String>, private val listener: Listener)
    : RecyclerView.Adapter<RamsAdapter.Holder>() {

    interface Listener {

        fun setOnRamClickListener(buttonFinishText: String)

    }

    class Holder(val binding: LayoutCardViewPhoneStorageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<String>, position: Int, listener: Listener) {

            binding.button.text = list[position]
            binding.button.setOnClickListener {

                val buttonFinishText = binding.button.text.toString()
                listener.setOnRamClickListener(buttonFinishText)
            }

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