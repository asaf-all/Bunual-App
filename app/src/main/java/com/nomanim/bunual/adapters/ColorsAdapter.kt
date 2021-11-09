package com.nomanim.bunual.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bunual.databinding.LayoutCardViewPhoneColorBinding
import com.nomanim.bunual.models.ModelColors

class ColorsAdapter(private val list: ArrayList<ModelColors>, private val listener: Listener)
    : RecyclerView.Adapter<ColorsAdapter.Holder>() {

    interface Listener {

        fun setOnColorClickListener(buttonFinishText: String)

    }

    class Holder(val binding: LayoutCardViewPhoneColorBinding) : RecyclerView.ViewHolder(binding.root) {

        fun run(list: ArrayList<ModelColors>, position: Int, listener: Listener) {

            binding.button.text = list[position].colorName
            binding.button.setTextColor(list[position].colorCode)

            binding.button.setOnClickListener {

                val buttonFinishText = binding.button.text.toString()
                listener.setOnColorClickListener(buttonFinishText)
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