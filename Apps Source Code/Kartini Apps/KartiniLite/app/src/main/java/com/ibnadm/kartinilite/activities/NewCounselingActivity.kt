package com.ibnadm.kartinilite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Counselor
import com.ibnadm.kartinilite.models.User
import com.ibnadm.kartinilite.utils.CounselorItem
import com.ibnadm.kartinilite.utils.UserItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_counseling.*
import kotlinx.android.synthetic.main.activity_new_message.*

class NewCounselingActivity : AppCompatActivity() {
    val TAG = "NewCounselingActivity"

    //Firebase Auth Object.
    private var mAuth: FirebaseAuth? = null
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_counseling)
        mAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference.child("counselors")
        btn_back_selectcouns.setOnClickListener{
            onBackPressed()
        }
        fetchUser()
    }
    companion object {
        val USER_KEY = "USER_KEY"
    }
    private fun fetchUser() {
        val fetchListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                for (child in p0.children) {
                    Log.d(TAG,child.toString())
                    val counselor = child.getValue(Counselor::class.java)
                    if (counselor != null){
                        adapter.add(CounselorItem(counselor))
                    }
                }
                adapter.setOnItemClickListener{ item, view ->
                    val counselorItem = item as CounselorItem
                    val intent = Intent(view.context, CounselingLogActivity::class.java)
                    intent.putExtra(USER_KEY, counselorItem.counselor)
                    startActivity(intent)
                    finish()
                }
                rv_new_counseling.adapter = adapter
            }

        }
        dbRef.addListenerForSingleValueEvent(fetchListener)
    }

    override fun onStart() {
        super.onStart()
        updateUI(mAuth!!.currentUser)
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}