package com.nomanim.bunual.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.ActivityAdsDetailsBinding
import com.nomanim.bunual.databinding.ActivityMainBinding

class AdsDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}