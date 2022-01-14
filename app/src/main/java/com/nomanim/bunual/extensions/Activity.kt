package com.nomanim.bunual.extensions

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

fun Activity.generateBitmapFromUri(imageUri: Uri): Bitmap {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(this.contentResolver, imageUri)
        ).copy(Bitmap.Config.RGBA_F16, true)
    } else {
        MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
    }
    return bitmap
}