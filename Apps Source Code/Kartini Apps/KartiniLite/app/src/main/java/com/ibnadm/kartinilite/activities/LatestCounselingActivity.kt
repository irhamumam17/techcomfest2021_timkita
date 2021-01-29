package com.ibnadm.kartinilite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Message
import com.ibnadm.kartinilite.models.User
import com.ibnadm.kartinilite.utils.LatestCounselingItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_counseling.*

class LatestCounselingActivity : AppCompatActivity() {
    val TAG = "LatestCounsActivity"
    private var mAuth: FirebaseAuth? = null
    private lateinit var dbRef: DatabaseReference
    companion object{
        var currentUser: User? = null
    }
    val adapter = GroupAdapter<ViewHolder>()
    val latestMessagesMap = HashMap<String, Message>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_counseling)
        mAuth = FirebaseAuth.getInstance()
        rv_latest_counseling.adapter = adapter
        rv_latest_counseling.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "Try to click adapter")
            val intent = Intent(this, CounselingLogActivity::class.java)
            val row = item as LatestCounselingItem
            intent.putExtra(NewCounselingActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        btn_backcouns.setOnClickListener {
            onBackPressed()
        }
        btn_create_cons.setOnClickListener {
            val intent = Intent(this, NewCounselingActivity::class.java)
            startActivity(intent)
        }
        fetchUser()
        fetchLatestCouns()
    }
    private fun fetchLatestCouns() {
        val fromUid = mAuth!!.currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference.child("latest-messages-user-counselor/$fromUid")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.d(TAG,"try to listent latest counseling")
                val message = p0.getValue(Message::class.java)
                latestMessagesMap[p0.key!!] = message!!
                refreshRecyclerViewMsg()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.d(TAG,"try to listent latest counseling")
                val message = p0.getValue(Message::class.java)
                latestMessagesMap[p0.key!!] = message!!
                refreshRecyclerViewMsg()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun refreshRecyclerViewMsg() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestCounselingItem(it))
        }
    }
    private fun fetchUser() {
        val uid = mAuth!!.currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference.child("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d(TAG,"Current User ${currentUser!!.fullName}")
            }

        })
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