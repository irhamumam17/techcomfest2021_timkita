package com.ibnadm.kartinilite.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    val TAG = "MainActivity"

    //firebase ref
    private var mAuth:FirebaseAuth? = null
    private var fusedLocationProviderClient:FusedLocationProviderClient? = null
    private lateinit var dbRef: DatabaseReference
    private var toast:Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        dbRef = FirebaseDatabase.getInstance().reference
            .child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        toast = Toast.makeText(
            this,
            "Silahkan Lengkapi profil anda untuk menggunakan feature Kartini !",
            Toast.LENGTH_SHORT
        )

        btn_profile.setOnClickListener(this)
        cv_reportnow.setOnClickListener(this)
        cv_whochats.setOnClickListener(this)
        cv_discuss.setOnClickListener(this)
        cv_consultnow.setOnClickListener(this)
        btn_emergency.setOnClickListener(this)


    }

    override fun onStart() {
        super.onStart()
        updateUI(mAuth!!.currentUser)
        setProfilePic()
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
    private fun setProfilePic() {
        val profileListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val profile = p0.getValue(com.ibnadm.kartinilite.models.User::class.java)
                profile?.let {
                    tv_welcome.text = getGreetingMessage()+", "+it.fullName+" !"
                    Picasso.get()
                        .load(it.photoUrl)
                        .into(btn_profile)

                }
            }

        }
        dbRef.addListenerForSingleValueEvent(profileListener)
    }
        private fun getGreetingMessage():String{
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        return when (timeOfDay) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..20 -> "Good Evening"
            in 21..23 -> "Good Night"
            else -> "Hello"
        }
    }

    override fun onClick(v: View?) {
        val cUid = mAuth!!.currentUser!!.uid
        val refCek = FirebaseDatabase.getInstance().reference.child("users/$cUid")
        refCek.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val profile = p0.getValue(User::class.java) ?: return
                when (v!!.id) {
                    R.id.btn_profile -> {
                        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.cv_reportnow -> {
                        if (profile.nik.isEmpty() || profile.nik == "-") {
                            toast!!.show()
                        } else {
                            val intent = Intent(this@MainActivity, LogReportActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    R.id.cv_whochats -> {
                        if (profile.nik.isEmpty() || profile.nik == "-") {
                            toast!!.show()
                        } else {
                            val intent =
                                Intent(this@MainActivity, LatestMessagesActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    R.id.cv_discuss -> {
                        val intent = Intent(this@MainActivity, ForumActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.cv_consultnow -> {
                        if (profile.nik.isEmpty() || profile.nik == "-") {
                            toast!!.show()
                        } else {
                            val intent = Intent(this@MainActivity, LatestCounselingActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    R.id.btn_emergency -> {
                        val currentUid = mAuth!!.currentUser!!.uid
                        val ref = FirebaseDatabase.getInstance().reference.child("users/$currentUid")
                        ref.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Log.d(TAG, p0.toException().toString())
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val user = p0.getValue(User::class.java) ?: return
                                if (user.trustedContact.isEmpty() || user.trustedContact == "-") {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Tolong lengkapi data anda dahulu !",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    if (ActivityCompat.checkSelfPermission(
                                            this@MainActivity,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        ActivityCompat.requestPermissions(
                                            this@MainActivity, arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                            ), 44
                                        )
                                    } else {
                                        fusedLocationProviderClient!!.lastLocation.addOnCompleteListener {
                                            var location = it.result
                                            if (location != null) {
                                                var geocoder = Geocoder(
                                                    this@MainActivity,
                                                    Locale.getDefault()
                                                )
                                                val addresses = geocoder.getFromLocation(
                                                    location.latitude,
                                                    location.longitude,
                                                    1
                                                )
                                                val getLatitude = addresses[0].latitude
                                                val getLongitude = addresses[0].longitude
                                                val getAddressLine = addresses[0].getAddressLine(0)
                                                val textSms = "EMERGENCY! Posisi ${user.fullName} sekarang ada di $getAddressLine, dengan koordinat latitude, longitude : $getLatitude, $getLongitude. Silahkan segera menuju kelokasi !"
                                                if (ActivityCompat.checkSelfPermission(
                                                        this@MainActivity,
                                                        Manifest.permission.SEND_SMS
                                                    ) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(
                                                        this@MainActivity, arrayOf(Manifest.permission.SEND_SMS), 2
                                                    )
                                                } else {
                                                    Log.d(TAG, textSms)
                                                    val smsManager = SmsManager.getDefault() as SmsManager
                                                    smsManager.sendTextMessage(user.trustedContact, null, textSms, null, null)
                                                    if (ActivityCompat.checkSelfPermission(
                                                            this@MainActivity,
                                                            Manifest.permission.CALL_PHONE
                                                        ) != PackageManager.PERMISSION_GRANTED) {
                                                        ActivityCompat.requestPermissions(
                                                            this@MainActivity, arrayOf(Manifest.permission.CALL_PHONE), 1
                                                        )
                                                    } else {
                                                        Log.d(TAG, "And Calling")
                                                        val trustedContact = user.trustedContact
                                                        val intent = Intent(
                                                            Intent.ACTION_CALL,
                                                            Uri.parse("tel:$trustedContact"))
                                                        startActivity(intent)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

        })
    }
}
