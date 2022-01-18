package com.nomanim.bunual.ui.fragments.adsdetailsactivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nomanim.bunual.adapters.GalleryPagerAdapter
import com.nomanim.bunual.databinding.ActivityGalleryImageBinding

class GalleryImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
        val photosJson = intent.getStringExtra("photos")
        val position = intent.getIntExtra("position", 0)
        val photos =
            Gson().fromJson<List<String>>(photosJson, object : TypeToken<List<String>>() {}.type)
        binding.galleryPager.adapter = GalleryPagerAdapter(this, this, photos)
        binding.galleryPager.setCurrentItem(position, false)
    }
}