package com.nomanim.bunual.ui.other

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.nomanim.bunual.R

private fun ImageView.downloadImageWithPicasso(uri: String) {

    /*Picasso
        .get()
        .load(uri.toUri())
        .placeholder(R.drawable.im_whenloadingpictures)
        .into(this)*/
}

fun ImageView.downloadImageWithGlide(view: View,uri: String?) {

    Glide
        .with(view)
        .load(uri?.toUri())
        .centerCrop()
        .placeholder(R.drawable.im_whenloadingpictures)
        .into(this)
}