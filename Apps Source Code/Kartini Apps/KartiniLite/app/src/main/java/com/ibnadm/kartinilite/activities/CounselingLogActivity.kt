package com.ibnadm.kartinilite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Counselor
import com.ibnadm.kartinilite.models.Message
import com.ibnadm.kartinilite.models.User
import com.ibnadm.kartinilite.utils.ChatFromCounsItem
import com.ibnadm.kartinilite.utils.ChatFromItem
import com.ibnadm.kartinilite.utils.ChatToItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_counseling_log.*

class CounselingLogActivity : AppCompatActivity() {
    val TAG = "ChatLogActivity"
    private var mAuth: FirebaseAuth? = null

    val adapter = GroupAdapter<ViewHolder>()

    var toCouns: Counselor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counseling_log)
        mAuth = FirebaseAuth.getInstance()
        rv_counsitems.adapter = adapter
        toCouns = intent.getParcelableExtra<Counselor>(NewCounselingActivity.USER_KEY)
        tv_name_oncouns.text = toCouns!!.fullName
        btn_back_selectcouns2.setOnClickListener {
            onBackPressed()
        }
        btn_send_msg_couns.setOnClickListener {
            performeSendMessage()
        }
        listenMessagesCouns()
    }
    private fun listenMessagesCouns() {
        val fromUid = mAuth!!.currentUser!!.uid
        val toUid = toCouns!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-counselor-messages/$fromUid/$toUid")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java) ?: return
                Log.d(TAG, message.text)
                if (message.fromUid == mAuth!!.currentUser!!.uid) {
                    val currentUser = LatestCounselingActivity.currentUser ?: return
                    adapter.add(ChatToItem(message,currentUser))
                } else {
                    adapter.add(ChatFromCounsItem(message, toCouns!!))
                }

                rv_counsitems.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun performeSendMessage() {
        val fromUid = mAuth!!.currentUser!!.uid
        val toUid = toCouns!!.uid
        val ref = FirebaseDatabase.getInstance().reference.child("user-counselor-messages/$fromUid/$toUid").push()
        val toRef = FirebaseDatabase.getInstance().reference.child("user-counselor-messages/$toUid/$fromUid").push()

        mAuth!!.currentUser ?: return
        val message = Message()
        message.id = ref.key!!
        message.text = et_enter_msg_couns.text.toString()
        message.fromUid = fromUid
        message.toUid = toUid
        message.timestamp = (System.currentTimeMillis())
        ref.setValue(message).addOnSuccessListener {
            Log.d(TAG, "Saved our chat message : ${ref.key}")
            et_enter_msg_couns.text.clear()
            rv_counsitems.scrollToPosition(adapter.itemCount - 1)
        }
        toRef.setValue(message)
        val latestMsgRef = FirebaseDatabase.getInstance().reference.child("latest-messages-user-counselor/$fromUid/$toUid")
        val latestMsgToRef = FirebaseDatabase.getInstance().reference.child("latest-messages-user-counselor/$toUid/$fromUid")
        latestMsgRef.setValue(message)
        latestMsgToRef.setValue(message)
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