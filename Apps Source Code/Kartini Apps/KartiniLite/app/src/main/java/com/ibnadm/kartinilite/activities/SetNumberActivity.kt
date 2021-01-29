package com.ibnadm.kartinilite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.ibnadm.kartinilite.R
import kotlinx.android.synthetic.main.activity_set_number.*
import kotlinx.android.synthetic.main.activity_verify_phone.*
import java.util.concurrent.TimeUnit

class SetNumberActivity : AppCompatActivity() {
    val TAG = "SetNumberActivity"
    lateinit var auth: FirebaseAuth
    companion object {
        val NUMBER_KEY = "NUMBER_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_number)
        btn_send_tc.setOnClickListener{
            val tc = et_number_phone.text.toString().trim()
            if (tc.isEmpty()) {
                Toast.makeText(this, "Enter Phone Mobile", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, VerifyPhoneActivity::class.java)
            intent.putExtra(SetNumberActivity.NUMBER_KEY, tc)
            startActivity(intent)
        }
        btn_backprofile.setOnClickListener {
            onBackPressed()
        }

    }
}