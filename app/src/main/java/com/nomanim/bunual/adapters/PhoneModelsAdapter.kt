package com.nomanim.bunual.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bunual.databinding.LayoutCardViewPhoneModelsBinding
import com.nomanim.bunual.api.entity.ModelsResponse
import com.nomanim.bunual.base.downloadImageWithGlide
import com.thekhaeng.pushdownanim.PushDownAnim

class PhoneModelsAdapter(
    val context: Context,
    private val list: ArrayList<ModelsResponse.Body>,
    val listener: Listener
) : RecyclerView.Adapter<PhoneModelsAdapter.Holder>() {

    interface Listener {
        fun onCardViewClickListener(modelName: String)
    }

    class Holder(val binding: LayoutCardViewPhoneModelsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun run(
            list: ArrayList<ModelsResponse.Body>,
            position: Int,
            listener: Listener,
            context: Context
        ) {
            binding.brandOrModelName.text = list[position].modelName
            binding.modelImageView.downloadImageWithGlide(itemView, list[position].modelImage)
            PushDownAnim.setPushDownAnimTo(binding.modelOrBrandCardView).setOnClickListener {
                listener.onCardViewClickListener(list[position].modelName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutCardViewPhoneModelsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.run(list, position, listener, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}