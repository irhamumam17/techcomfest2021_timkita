package com.ibnadm.kartinilite.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.utils.BaseActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    val TAG = "ProfileActivity"
    //Firebase Auth Object.
    private var mAuth:FirebaseAuth? = null

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference
            .child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)

        et_email.isEnabled = false
        et_tc.isEnabled = false
        setProfileInfo()
        btn_update.setOnClickListener {
            updateProfile()
        }
        btn_logout.setOnClickListener {
            signingOut()
        }
        btn_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        btn_set_tc.setOnClickListener {
            val intent = Intent(this, SetNumberActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setProfileInfo() {
        val profileListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val profile = p0.getValue(com.ibnadm.kartinilite.models.User::class.java)
                profile?.let {
                    tv_hello.text = "Hello, "+it.fullName
                    et_name.setText(it.fullName)
                    et_email.setText(it.email)
                    if (it.address == null || it.address == "") {
                        it.address = "-"
                    }
                    et_address.setText(it.address)
                    if (it.nik == null || it.nik == ""){
                        it.nik = "-"
                    } else {
                        et_nik.isEnabled = false
                    }
                    Log.d(TAG, it.nik)
                    et_nik.setText(it.nik)
                    if (it.trustedContact == null || it.trustedContact == ""){
                        it.trustedContact = "Please Set Your Trusted Contact"
                    }
                    et_tc.setText(it.trustedContact)
                    if (it.phoneNumber == null|| it.phoneNumber == "null" || it.phoneNumber == ""){
                        it.phoneNumber = "-"
                    }
                    et_cp.setText(it.phoneNumber)
                    Picasso.get()
                        .load(it.photoUrl)
                        .into(iv_profile)

                }
            }

        }
        dbRef.addListenerForSingleValueEvent(profileListener)
    }
    private fun updateProfile() {
        val profileListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val profile = p0.getValue(com.ibnadm.kartinilite.models.User::class.java)
                profile?.let {
                    val user = com.ibnadm.kartinilite.models.User()
                    user.fullName = et_name.text.toString().trim()
                    user.address = et_address.text.toString().trim()
                    user.phoneNumber = et_cp.text.toString().trim()
                    user.trustedContact = profile.trustedContact
                    if (et_nik.length() != 16) {
                        Toast.makeText(
                            this@ProfileActivity,
                            "NIK yang diinputkan tidak valid !",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        user.nik = et_nik.text.toString().trim()
                        user.sex = "Female"
                        user.email = et_email.text.toString().trim()
                        user.photoUrl = profile.photoUrl
                        user.uid = profile.uid
                        dbRef.setValue(user).addOnSuccessListener {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Profile berhasil di update !",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            et_nik.isEnabled = false
                            if ((user.nik.substring(7..8).toInt()) < 40) {
                                val dbUserWarning = FirebaseDatabase.getInstance().reference
                                    .child("users-warning-male-detection").child(profile.uid)
                                dbUserWarning.setValue(user).addOnSuccessListener {
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        "Jenis kelamin anda terdeteksi Laki-Laki, untuk itu akun anda kami suspend dalam batas waktu 1 x 24jam. untuk lebih lanjut silahkan hubungi Admin Kartini",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }
                    }
                }
            }
        }
        dbRef.addListenerForSingleValueEvent(profileListener)
    }

    private fun signingOut() {
        mAuth!!.signOut()
        LoginManager.getInstance().logOut()
        updateUI(null)
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI(mAuth!!.currentUser)
    }


}
