package com.nomanim.bax.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.nomanim.bax.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        //getPhoneApiLink("phone_brands_link","phoneBrandsApiLink")
        //getPhoneApiLink("phone_models_link","phoneModelsApiLink")


    }

    private fun getPhoneApiLink(documentName: String,spFileName: String) {

        firestore.collection("Links").document(documentName).get().addOnSuccessListener { value ->

            val link = value.getField<String>("link")
            link?.let { saveToSharedPreferences(it,spFileName) }
            intentToMainActivity()

        }
    }

    private fun saveToSharedPreferences(link: String,fileName: String) {

        val sp = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(fileName,link)
        editor.apply()

    }

    private fun intentToMainActivity() {

        val intent = Intent(this@SplashScreenActivity,MainActivity::class.java)
        finish()
        startActivity(intent)

    }
}