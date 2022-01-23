package com.nomanim.bunual.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bunual.databinding.LayoutCardViewAllPhonesBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.base.downloadImageWithGlide
import com.thekhaeng.pushdownanim.PushDownAnim

class AllPhonesAdapter(
    val context: Context,
    val list: List<ModelAnnouncement>,
    val listener: Listener,
    private val onClick: ((ModelAnnouncement) -> Unit)
) : RecyclerView.Adapter<AllPhonesAdapter.Holder>() {

    interface Listener {

    }

    class Holder(val binding: LayoutCardViewAllPhonesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun run(
            list: List<ModelAnnouncement>,
            position: Int,
            listener: Listener,
            onClick: (ModelAnnouncement) -> Unit
        ) {

            binding.announcementPrice.text = list[position].phone.price
            binding.announcementName.text = list[position].phone.brand
            binding.announcementDescription.text = list[position].description
            binding.announcementViews.text = list[position].numberOfViews
            binding.announcementImageView.downloadImageWithGlide(
                binding.root,
                list[position].image[0]
            )
            PushDownAnim.setPushDownAnimTo(binding.verticalLinearLayout).setOnClickListener {
                onClick.invoke(list[position])
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutCardViewAllPhonesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.run(list, position, listener, onClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}