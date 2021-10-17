package com.nomanim.bunual.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nomanim.bunual.databinding.ActivityNewAnnouncementBinding

class NewAnnouncementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAnnouncementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAnnouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}