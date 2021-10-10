package com.nomanim.bax.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nomanim.bax.databinding.LayoutCardViewImageSliderBinding
import com.nomanim.bax.ui.other.downloadImageWithGlide
import com.smarteist.autoimageslider.SliderViewAdapter

class ImagesSliderAdapter(val imagesList: List<String>) : SliderViewAdapter<ImagesSliderAdapter.SliderAdapter>() {

    class SliderAdapter(val binding: LayoutCardViewImageSliderBinding) : SliderViewAdapter.ViewHolder(binding.root) {

        fun setImages(imagesList: List<String>, position: Int) {

            binding.imageViewForSlider.downloadImageWithGlide(binding.root,imagesList[position])
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