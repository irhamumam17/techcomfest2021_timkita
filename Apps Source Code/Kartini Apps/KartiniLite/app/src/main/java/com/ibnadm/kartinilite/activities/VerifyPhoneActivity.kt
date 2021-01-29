package com.ibnadm.kartinilite.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Otp
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_verify_phone.*
import java.util.concurrent.TimeUnit

class VerifyPhoneActivity : AppCompatActivity() {
    val TAG = "VerifyPhoneActivity"
    var numberPhone: String? = null
    var verificationCodeBySystem: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        val otpCode = et_otp_number.text.toString().trim()
        numberPhone = intent.getStringExtra(SetNumberActivity.NUMBER_KEY)
        Log.d(TAG, "isi number => $numberPhone")
        btn_backtc.setOnClickListener {
            onBackPressed()
        }
        sendVerifivicationCodeToUser(numberPhone)
        btn_verify_tc.setOnClickListener {
            verifyCode(otpCode)
        }
        verificationCodeBySystem = otpCode
    }

    private fun sendVerifivicationCodeToUser(numberPhone: String?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+62$numberPhone",
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks
        );
    }
    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            val code: String? = p0.smsCode
            if (code != null) {
                verificationCodeBySystem = code
            }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            when (p0) {
                is FirebaseAuthInvalidCredentialsException -> {
                    makeText(this@VerifyPhoneActivity, "Nomor yang anda masukan salah!", LENGTH_SHORT).show()
                    onBackPressed()
                }
                is FirebaseTooManyRequestsException -> {
                    makeText(this@VerifyPhoneActivity, "Quota SMS telah habis silahkan coba lagi besok!", LENGTH_SHORT).show()
                }
                else -> {
                    makeText(this@VerifyPhoneActivity, p0.toString(), LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun verifyCode(code: String) {
        if (code == verificationCodeBySystem) {
            val dbRef = FirebaseDatabase.getInstance().reference
                .child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            dbRef.addListenerForSingleValueEvent(object :  ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val user: com.ibnadm.kartinilite.models.User = p0.getValue(com.ibnadm.kartinilite.models.User::class.java) ?: return
                    val userNow = com.ibnadm.kartinilite.models.User()
                    userNow.fullName = user.fullName
                    userNow.address = user.address
                    userNow.phoneNumber = user.phoneNumber
                    userNow.trustedContact = "+62$numberPhone"
                    userNow.sex = user.sex
                    userNow.email = user.email
                    userNow.photoUrl = user.photoUrl
                    userNow.uid = user.uid
                    dbRef.setValue(userNow).addOnSuccessListener {
                        makeText(this@VerifyPhoneActivity, "Trusted Contact berhasil di update !", LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@VerifyPhoneActivity, ProfileActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d(TAG, p0.toException().toString())
                }

            })
        } else {
            makeText(this@VerifyPhoneActivity, "Update Trusted Contact Gagal. Silahkan Coba Kembali !", LENGTH_SHORT).show()
        }
    }
}