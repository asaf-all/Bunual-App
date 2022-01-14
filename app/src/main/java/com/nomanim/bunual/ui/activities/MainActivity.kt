package com.nomanim.bunual.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.nomanim.bunual.R
import com.nomanim.bunual.databinding.ActivityMainBinding
import com.nomanim.bunual.models.ModelAnnouncement

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectBottomNavWithNavHost()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()


    }

    override fun onPause() {
        super.onPause()


    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()


    }

    fun intentToAdsDetails(model: ModelAnnouncement) {
        val intent = Intent(this, AdsDetailsActivity::class.java)
        val extraData = Intent()
        extraData.putExtra("announcement",model)
        intent.putExtras(extraData)
        startActivity(intent)
    }

    private fun connectBottomNavWithNavHost() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainActivityHost) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }

}