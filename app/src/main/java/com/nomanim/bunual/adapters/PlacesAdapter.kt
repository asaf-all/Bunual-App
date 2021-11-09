package com.nomanim.bunual.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bunual.databinding.LayoutCardViewPhoneColorBinding
import com.nomanim.bunual.retrofit.models.ModelPlaces

class PlacesAdapter(private val list: List<ModelPlaces>, private val listener: Listener)
    : RecyclerView.Adapter<PlacesAdapter.Holder>() {

    interface Listener {

        fun setOnPlaceClickListener(buttonFinishText: String)

    }

    class Holder(val binding: LayoutCardViewPhoneColorBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: List<ModelPlaces>, position: Int, listener: Listener) {

            binding.button.text = list[position].city
            binding.button.setOnClickListener {

                val buttonFinishText = binding.button.text.toString()
                listener.setOnPlaceClickListener(buttonFinishText)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutCardViewPhoneColorBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.run(list,position,listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}