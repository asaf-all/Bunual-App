package com.nomanim.bunual.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.nomanim.bunual.R
import com.nomanim.bunual.base.downloadImageWithGlide

class GalleryPagerAdapter(
    private val activity: Activity,
    private val context: Context,
    private val items: List<String>
) : RecyclerView.Adapter<GalleryPagerAdapter.GalleryPagerHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GalleryPagerAdapter.GalleryPagerHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.layout_gallery_pager,
            parent,
            false
        )
        return GalleryPagerHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryPagerAdapter.GalleryPagerHolder, position: Int) {
        val photo = items[holder.absoluteAdapterPosition]
        val view: View = activity.findViewById(android.R.id.content)
        Glide
            .with(view)
            .load(photo)
            .placeholder(R.drawable.im_whenloadingpictures)
            .into(holder.photoImg)
    }

    override fun getItemCount() = items.size

    inner class GalleryPagerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImg: PhotoView = itemView.findViewById(R.id.photoImg)
    }

}