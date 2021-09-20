package com.nomanim.bax.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.nomanim.bax.R
import com.nomanim.bax.databinding.ActivityMainBinding
import com.nomanim.bax.databinding.ActivityNewAnnouncementBinding

class NewAnnouncementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAnnouncementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAnnouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}