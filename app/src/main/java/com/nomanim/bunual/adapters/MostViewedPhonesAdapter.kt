package com.nomanim.bunual.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nomanim.bunual.databinding.LayoutCardViewMostviewedPhonesBinding
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.base.downloadImageWithGlide
import com.thekhaeng.pushdownanim.PushDownAnim

class MostViewedPhonesAdapter(
    private val items: ArrayList<ModelAnnouncement>,
    val listener: Listener,
    private val onClick: ((ModelAnnouncement) -> Unit)
) : RecyclerView.Adapter<MostViewedPhonesAdapter.Holder>() {

    interface Listener {

    }

    class Holder(val binding: LayoutCardViewMostviewedPhonesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun run(
            list: ArrayList<ModelAnnouncement>,
            position: Int,
            listener: Listener,
            onClick: (ModelAnnouncement) -> Unit
        ) {

            binding.mostViewedPhonesPrice.text = list[position].phone.price
            binding.mostViewedPhonesName.text = list[position].phone.brand
            binding.mostViewedPhonesViews.text = list[position].numberOfViews
            binding.phoneImage.downloadImageWithGlide(binding.root, list[position].image[0])
            PushDownAnim.setPushDownAnimTo(binding.mostViewedPhonesLinearLayout)
                .setOnClickListener {
                    onClick.invoke(list[position])
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(
            LayoutCardViewMostviewedPhonesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.run(items, position, listener, onClick)
    }

    override fun getItemCount(): Int {
        return items.size
    }


}