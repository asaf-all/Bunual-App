package com.nomanim.bunual.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.nomanim.bunual.databinding.LayoutCardViewImageSliderBinding
import com.nomanim.bunual.base.downloadImageWithGlide
import com.smarteist.autoimageslider.SliderViewAdapter

class ImagesSliderAdapter(private val imagesList: ArrayList<String>) : SliderViewAdapter<ImagesSliderAdapter.SliderAdapter>() {

    class SliderAdapter(val binding: LayoutCardViewImageSliderBinding) : SliderViewAdapter.ViewHolder(binding.root) {

        fun setImages(imagesList: ArrayList<String>, position: Int) {

            binding.imageViewForSlider.downloadImageWithGlide(binding.root,imagesList.elementAt(position))
        }
    }

    override fun getCount(): Int {

        return imagesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapter {

        val inflate = LayoutCardViewImageSliderBinding.inflate(LayoutInflater.from(parent?.context),parent,false)
        return SliderAdapter(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapter?, position: Int) {

        viewHolder?.setImages(imagesList,position)
    }
}