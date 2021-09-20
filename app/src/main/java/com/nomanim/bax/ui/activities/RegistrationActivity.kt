package com.nomanim.bax.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nomanim.bax.R
import com.nomanim.bax.databinding.ActivityRegistrationBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var token: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeUIWhenActivityCreate()
        auth = FirebaseAuth.getInstance()
        binding.countryCodePicker.registerCarrierNumberEditText(binding.phoneNumberEditText)

        binding.backButton.setOnClickListener { onBackPressed() }
        binding.createAccountNextButton.setOnClickListener { view -> nextButtonAlgorithm(view) }
        binding.confirmationCodeButton.setOnClickListener { view -> confirmationButtonAlgorithm(view) }
        binding.sendAgainTextView.setOnClickListener { view -> requestAgainConfirmationCode(view) }

    }

    private fun requestConfirmationCode(mView: View) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(binding.countryCodePicker.fullNumberWithPlus)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this@RegistrationActivity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(mVerificationId: String, mToken: PhoneAuthProvider.ForceResendingToken) {

                    verificationId = mVerificationId
                    token = mToken
                    changeUIWhenCodeSend()
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                    signInWithCredential(credential,mView)
                }

                override fun onVerificationFailed(exception: FirebaseException) {

                    binding.createAccountProgressBar.visibility = View.INVISIBLE
                    binding.createAccountNextButton.visibility = View.VISIBLE
                    Snackbar.make(mView,resources.getString(R.string.enter_valid_phone_number), Snackbar.LENGTH_LONG).show()
                    exception.localizedMessage
                }

            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun requestAgainConfirmationCode(mView: View) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(binding.countryCodePicker.fullNumberWithPlus)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this@RegistrationActivity)
            .setForceResendingToken(token!!)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(mVerificationId: String, mToken: PhoneAuthProvider.ForceResendingToken) {

                    verificationId = mVerificationId
                    token = mToken
                    changeUIWhenCodeSend()
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                    signInWithCredential(credential,mView)
                }

                override fun onVerificationFailed(exception: FirebaseException) {

                    binding.createAccountProgressBar.visibility = View.INVISIBLE
                    binding.createAccountNextButton.visibility = View.VISIBLE
                    Snackbar.make(mView,resources.getString(R.string.enter_valid_phone_number), Snackbar.LENGTH_LONG).show()
                    exception.localizedMessage
                }

            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun signInWithCredential(credential: PhoneAuthCredential, mView: View) {

        auth.signInWithCredential(credential).addOnSuccessListener {

            val intentToMainActivity = Intent(this@RegistrationActivity,MainActivity::class.java)
            finish()
            startActivity(intentToMainActivity)
            Toast.makeText(this@RegistrationActivity,resources.getString(R.string.account_opened_successfully),Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->

            Snackbar.make(mView,resources.getString(R.string.enter_correct_confirmation_code), Snackbar.LENGTH_LONG).show()
            exception.localizedMessage
        }

    }

    private fun changeUIWhenActivityCreate() {

        binding.enterCodeLayout.visibility = View.INVISIBLE
        binding.confirmationCodeButton.visibility = View.INVISIBLE
        binding.createAccountProgressBar.visibility = View.INVISIBLE
        binding.confirmationCodeButton.visibility = View.INVISIBLE

    }

    private fun changeUIWhenCodeSend() {

        binding.createAccountProgressBar.visibility = View.INVISIBLE
        binding.enterPhoneNumberLayout.visibility = View.GONE
        binding.enterCodeLayout.visibility = View.VISIBLE
        binding.createAccountNextButton.visibility = View.INVISIBLE
        binding.confirmationCodeButton.visibility = View.VISIBLE

    }

    private fun nextButtonAlgorithm(mView: View) {

        if (TextUtils.isEmpty(binding.phoneNumberEditText.text.toString())) {

            Snackbar.make(mView,resources.getString(R.string.enter_phone_number), Snackbar.LENGTH_SHORT).show()
        }else {

            binding.createAccountNextButton.visibility = View.INVISIBLE
            binding.createAccountProgressBar.visibility = View.VISIBLE
            requestConfirmationCode(mView)
        }
    }

    private fun confirmationButtonAlgorithm(mView: View) {

        if (TextUtils.isEmpty(binding.verificationCodeEditText.text.toString())) {

            Snackbar.make(
                mView,
                resources.getString(R.string.enter_confirmation_code),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {

            lifecycleScope.launch {

                binding.confirmationCodeButton.isEnabled = false
                delay(4000)
                binding.confirmationCodeButton.isEnabled = true
            }
            val code = binding.verificationCodeEditText.text.toString().trim()
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithCredential(credential, mView)
        }
    }

}