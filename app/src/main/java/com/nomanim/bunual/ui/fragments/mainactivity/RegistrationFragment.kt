package com.nomanim.bunual.ui.fragments.mainactivity

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nomanim.bunual.R
import com.nomanim.bunual.base.BaseFragment
import com.nomanim.bunual.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RegistrationFragment : BaseFragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var token: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding.countryCodePicker.registerCarrierNumberEditText(binding.phoneNumberEditText)

        changeUIWhenActivityCreate()

        binding.createAccountNextButton.setOnClickListener {
            nextButtonAlgorithm(it)
        }
        binding.confirmationCodeButton.setOnClickListener {
            confirmationButtonAlgorithm(it)
        }
        binding.sendAgainTextView.setOnClickListener {
            requestAgainConfirmationCode(it, mMainActivity)
        }
    }

    private fun requestConfirmationCode(mView: View, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(binding.countryCodePicker.fullNumberWithPlus)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(
                    mVerificationId: String,
                    mToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = mVerificationId
                    token = mToken
                    changeUIWhenCodeSend()
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential, mView)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    binding.createAccountProgressBar.visibility = View.INVISIBLE
                    binding.createAccountNextButton.visibility = View.VISIBLE
                    Snackbar.make(
                        mView,
                        resources.getString(R.string.enter_valid_phone_number),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun requestAgainConfirmationCode(mView: View, activity: Activity) {
        binding.sendAgainprogressBar.visibility = View.VISIBLE
        binding.sendAgainTextView.visibility = View.INVISIBLE

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(binding.countryCodePicker.fullNumberWithPlus)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setForceResendingToken(token!!)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    mVerificationId: String,
                    mToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    binding.sendAgainprogressBar.visibility = View.INVISIBLE
                    binding.sendAgainTextView.visibility = View.VISIBLE
                    verificationId = mVerificationId
                    token = mToken
                    changeUIWhenCodeSend()
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential, mView)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    binding.sendAgainprogressBar.visibility = View.INVISIBLE
                    binding.sendAgainTextView.visibility = View.VISIBLE
                    binding.createAccountProgressBar.visibility = View.INVISIBLE
                    binding.createAccountNextButton.visibility = View.VISIBLE
                    Snackbar.make(
                        mView,
                        resources.getString(R.string.enter_valid_phone_number),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential, mView: View) {
        auth.signInWithCredential(credential).addOnSuccessListener {
            findNavController().navigate(R.id.action_registrationFragment_to_homeFragment)
            showToastMessage(getString(R.string.account_opened_successfully))

        }.addOnFailureListener { exception ->
            Snackbar.make(
                mView,
                resources.getString(R.string.enter_correct_confirmation_code),
                Snackbar.LENGTH_LONG
            ).show()
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
            Snackbar.make(
                mView,
                resources.getString(R.string.enter_phone_number),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            binding.createAccountNextButton.visibility = View.INVISIBLE
            binding.createAccountProgressBar.visibility = View.VISIBLE
            activity?.let { requestConfirmationCode(mView, it) }
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